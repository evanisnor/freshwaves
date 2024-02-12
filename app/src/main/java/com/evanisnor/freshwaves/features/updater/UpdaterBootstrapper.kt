package com.evanisnor.freshwaves.features.updater

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import com.evanisnor.freshwaves.features.updater.workmanager.WorkManagerDelegate
import com.evanisnor.freshwaves.integration.crashlytics.Crashlytics
import com.evanisnor.freshwaves.user.UserStateRepository
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class UpdaterBootstrapper
  @Inject
  constructor(
    private val workManager: WorkManagerDelegate,
    private val userStateRepository: UserStateRepository,
    private val crashlytics: Crashlytics,
  ) {
    companion object {
      val CONSTRAINTS: Constraints =
        Constraints.Builder()
          .setRequiredNetworkType(NetworkType.CONNECTED)
          .build()
    }

    fun updateNow() {
      enqueue(workRequest())
    }

    suspend fun runUpdateAfterAuthorization() {
      userStateRepository.currentState.collectLatest { userState ->
        when (userState) {
          is UserStateRepository.State.LoggedIn -> {
            crashlytics.setCustomKey("login", Instant.now().epochSecond)
            enqueue(workRequest())
          }
          UserStateRepository.State.NoUser -> Timber.i("User is not logged in. Update cannot run.")
        }
      }
    }

    fun scheduleNextUpdate(): Instant {
      val targetStartTime =
        ZonedDateTime.now(ZoneId.systemDefault())
          .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
          .withHour(5)
          .withMinute(0)
          .withSecond(0)
          .withNano(0)

      val delay =
        Duration.between(
          ZonedDateTime.now(ZoneId.systemDefault()),
          targetStartTime,
        )

      enqueue(workRequest(delay))

      return targetStartTime.toInstant()
    }

    private fun workRequest(delay: Duration = Duration.ZERO) =
      OneTimeWorkRequestBuilder<UpdateWorker>()
        .setConstraints(CONSTRAINTS)
        .setInitialDelay(delay)
        .build()

    private fun enqueue(workRequest: WorkRequest) = workManager.enqueue(workRequest)
  }
