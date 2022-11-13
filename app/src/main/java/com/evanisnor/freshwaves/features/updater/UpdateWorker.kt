package com.evanisnor.freshwaves.features.updater

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class UpdateWorker @AssistedInject constructor(
  @Assisted applicationContext: Context,
  @Assisted workerParameters: WorkerParameters,
  private val updaterRepository: UpdaterRepository,
  private val spotifyRepository: SpotifyRepository,
  private val updaterBootstrapper: UpdaterBootstrapper,
  private val freshAlbumNotifier: FreshAlbumNotifier,
) : CoroutineWorker(applicationContext, workerParameters) {

  override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
    var result = Result.success()
    updaterRepository.updateState(UpdaterState.Running)

    try {
      spotifyRepository.update()
      notifyOfNewAlbums()
    } catch (throwable: Throwable) {
      Timber.e("Failed to update cache: $throwable")
      Firebase.crashlytics.recordException(throwable)
      result = Result.failure()
    }

    finish(result)
    result
  }

  private suspend fun notifyOfNewAlbums() {
    val freshAlbums =
      spotifyRepository.getAlbumsReleasedAfter(Instant.now().startOfDayUTC())
    freshAlbumNotifier.send(freshAlbums)
  }

  private suspend fun finish(result: Result) {
    val nextRun = updaterBootstrapper.scheduleNextUpdate()

    updaterRepository.apply {
      val status = result.toStatus()
      updateState(status)
      updateLastRun(Instant.now())
      updateNextRun(nextRun)
      updateState(UpdaterState.Idle)
    }
  }

}
