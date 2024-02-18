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
import javax.inject.Inject

class UpdaterController
  @Inject
  constructor(
    private val updaterRepository: UpdaterRepository,
    private val spotifyRepository: SpotifyRepository,
    private val updaterBootstrapper: UpdaterBootstrapper,
    private val freshAlbumNotifier: FreshAlbumNotifier,
  ) {
    /**
     * Internal wrapper class to associate a [ListenableWorker.Result] with an internal [UpdaterState].
     * At some point, the ListenableWorker.Result API changed so the inner types became LIBRARY_GROUP
     * restricted for no apparent reason.
     */
    private data class Result(
      val workerResult: ListenableWorker.Result,
      val updaterState: UpdaterState,
    )

    suspend fun runUpdate(): ListenableWorker.Result =
      withContext(Dispatchers.Default) {
        var result = Result(ListenableWorker.Result.success(), UpdaterState.Success)
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

          freshAlbumNotifier.notifyOfNewAlbums()
        } catch (throwable: Throwable) {
          Timber.e("Failed to update cache: $throwable")
          Firebase.crashlytics.recordException(throwable)
          result = Result(ListenableWorker.Result.failure(), UpdaterState.Failure)
        }

        finish(result)
        result.workerResult
      }

    private suspend fun finish(result: Result) {
      val nextRun = updaterBootstrapper.scheduleNextUpdate()

      updaterRepository.apply {
        val status = result.updaterState
        updateState(status)
        updateLastRun(Instant.now())
        updateNextRun(nextRun)
        updateState(UpdaterState.Idle)
      }
    }
  }
