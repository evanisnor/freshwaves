package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
      update()
      notifyOfNewAlbums()
    } catch (throwable: Throwable) {
      Log.e(
        "UpdateWorker",
        "Failed to update cache: $throwable"
      )
      Firebase.crashlytics.recordException(throwable)
      result = Result.failure()
    }

    finish(result)
    result
  }

  private suspend fun update() {
    Log.i("UpdateWorker", "Fetching user profile")
    val userProfile = spotifyRepository.userProfile()

    Log.i("UpdateWorker", "Fetching top artists")
    spotifyRepository.updateTopArtists(120)

    Log.i("UpdateWorker", "Fetching albums...")
    spotifyRepository.getTopArtists().let { artists ->
      artists.forEach { artist ->
        Log.i("UpdateWorker", "Fetching albums for ${artist.name}")
        spotifyRepository.updateAlbums(artist, userProfile)
      }
    }

    Log.i("UpdateWorker", "Fetching missing tracks...")
    spotifyRepository.getLatestAlbumsMissingTracks().let { albums ->
      albums.forEach { album ->
        Log.i(
          "UpdateWorker",
          "Fetching tracks for ${album.artist?.name ?: "???"} - ${album.name}"
        )
        spotifyRepository.updateTracks(album)
      }
    }
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
