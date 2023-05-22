package com.evanisnor.freshwaves.features.updater

import androidx.work.ListenableWorker
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class UpdaterController @Inject constructor(
  private val updaterRepository: UpdaterRepository,
  private val spotifyRepository: SpotifyRepository,
  private val updaterBootstrapper: UpdaterBootstrapper,
  private val freshAlbumNotifier: FreshAlbumNotifier,
) {

  suspend fun runUpdate(): ListenableWorker.Result = withContext(Dispatchers.Default) {
    var result = ListenableWorker.Result.success()
    updaterRepository.updateState(UpdaterState.Running)

    try {
      Timber.d("Fetching user profile")
      val userProfile = spotifyRepository.userProfile()

      Timber.d("Fetching top artists")
      spotifyRepository.updateTopArtists(120)
      val artists = spotifyRepository.getTopArtists()

      Timber.d("Fetching albums...")
      artists.forEach { artist ->
        Timber.d("Fetching albums for ${artist.name}")
        spotifyRepository.updateAlbums(artist, userProfile)
      }

      Timber.d("Fetching missing tracks...")
      spotifyRepository.getLatestAlbumsMissingTracks().let { albums ->
        albums.forEach { album ->
          Timber.d("Fetching tracks for ${album.artist?.name ?: "???"} - ${album.name}")
          spotifyRepository.updateTracks(album)
        }
      }

      notifyOfNewAlbums()
    } catch (throwable: Throwable) {
      Timber.e("Failed to update cache: $throwable")
      Firebase.crashlytics.recordException(throwable)
      result = ListenableWorker.Result.failure()
    }

    finish(result)
    result
  }

  private suspend fun notifyOfNewAlbums() {
    // Check for new albums released since the day after the last update, or just check from today.
    val checkDate = updaterRepository.lastRunOn()?.plus(1, ChronoUnit.DAYS) ?: Instant.now()
    val freshAlbums = spotifyRepository.getAlbumsReleasedAfter(checkDate.startOfDayUTC())
    freshAlbumNotifier.send(freshAlbums)
  }

  private suspend fun finish(result: ListenableWorker.Result) {
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
