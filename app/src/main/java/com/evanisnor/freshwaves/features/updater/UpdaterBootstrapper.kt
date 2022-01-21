package com.evanisnor.freshwaves.features.updater

import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import java.time.*
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class UpdaterBootstrapper @Inject constructor(
    private val workManager: WorkManager,
    private val localBroadcastManager: LocalBroadcastManager
) {

    companion object {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    fun updateNow() {
        enqueue(workRequest())
    }

    fun registerForSuccessfulAuthorization() {
        localBroadcastManager.register(
            intentFilter = IntentFilter(SpotifyAuthorization.authorizationSuccessfulAction),
            receiver = {
                enqueue(workRequest())
            }
        )
    }

    fun scheduleNextUpdate(): Instant {
        val targetStartTime = ZonedDateTime.now(ZoneId.systemDefault())
            .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
            .withHour(5)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val delay = Duration.between(
            ZonedDateTime.now(ZoneId.systemDefault()),
            targetStartTime
        )

        enqueue(workRequest(delay))

        return targetStartTime.toInstant()
    }

    private fun workRequest(delay: Duration = Duration.ZERO) =
        OneTimeWorkRequestBuilder<UpdateWorker>()
            .setConstraints(constraints)
            .setInitialDelay(delay)
            .build()

    private fun enqueue(workRequest: WorkRequest) = workManager.enqueue(workRequest)

}