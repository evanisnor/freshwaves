package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.time.Instant

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val spotifyUserRepository: SpotifyUserRepository,
    private val spotifyArtistRepository: SpotifyArtistRepository,
    private val spotifyAlbumRepository: SpotifyAlbumRepository,
    private val updaterBootstrapper: UpdaterBootstrapper,
    private val freshAlbumNotifier: FreshAlbumNotifier
) : CoroutineWorker(applicationContext, workerParameters) {


    override suspend fun doWork(): Result {
        var result = Result.success()
        updaterBootstrapper.broadcastState(applicationContext, UpdaterState.Running)

        withContext(Dispatchers.Default) {
            try {
                update()
            } catch (throwable: Throwable) {
                Log.e(
                    "UpdateWorker",
                    "Failed to update cache: $throwable"
                )
                Firebase.crashlytics.recordException(throwable)
                result = Result.failure()
            }
        }

        notifyOfNewAlbums()

        with(applicationContext) {
            updaterBootstrapper.scheduleNextUpdate(this)
            updaterBootstrapper.broadcastState(this, result.toStatus())
        }

        return result
    }

    private suspend fun update() {
        Log.i("UpdateWorker", "Fetching user profile")
        val userProfile = spotifyUserRepository.userProfile()

        Log.i("UpdateWorker", "Fetching top artists")
        spotifyArtistRepository.updateTopArtists(120)

        Log.i("UpdateWorker", "Fetching albums...")
        spotifyArtistRepository.getTopArtists().let { artists ->
            artists.forEach { artist ->
                Log.i("UpdateWorker", "Fetching albums for ${artist.name}")
                spotifyAlbumRepository.updateAlbums(artist, userProfile)
            }
        }

        Log.i("UpdateWorker", "Fetching missing tracks...")
        spotifyAlbumRepository.getLatestAlbumsMissingTracks().let { albums ->
            albums.forEach { album ->
                Log.i(
                    "UpdateWorker",
                    "Fetching tracks for ${album.artist?.name ?: "???"} - ${album.name}"
                )
                spotifyAlbumRepository.updateTracks(album)
            }
        }
    }

    private suspend fun notifyOfNewAlbums() {
        val freshAlbums =
            spotifyAlbumRepository.getAlbumsReleasedAfter(Instant.now().startOfDayUTC())

        if (freshAlbums.isNotEmpty()) {
            freshAlbums.forEach { album ->
                val albumNotification = freshAlbumNotifier.buildAlbumNotification(album)
                freshAlbumNotifier.send(album, albumNotification)
            }

            val messageNotification = freshAlbumNotifier.buildMessageNotification(freshAlbums)
            freshAlbumNotifier.send(messageNotification)
        }
    }

}
