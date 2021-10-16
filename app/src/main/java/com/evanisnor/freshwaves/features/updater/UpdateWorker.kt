package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import okhttp3.internal.wait

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val spotifyUserRepository: SpotifyUserRepository,
    private val spotifyArtistRepository: SpotifyArtistRepository,
    private val spotifyAlbumRepository: SpotifyAlbumRepository,
    private val updaterBootstrapper: UpdaterBootstrapper
) : CoroutineWorker(applicationContext, workerParameters) {

    override suspend fun doWork(): Result {
        var result = Result.success()

        withContext(Dispatchers.Default) {
            try {
                update()
            } catch (throwable: Throwable) {
                Log.e(
                    "UpdateWorker",
                    "Failed to update cache: $throwable\n${throwable.stackTraceToString()}"
                )
                result = Result.failure()
            }
        }

        updaterBootstrapper.scheduleNextUpdate(applicationContext)
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

        Log.i("UpdateWorker", "Fetching tracks...")
        spotifyAlbumRepository.getAlbums().let { albums ->
            albums.forEach { album ->
                Log.i(
                    "UpdateWorker",
                    "Fetching tracks for ${album.artist?.name ?: "???"} - ${album.name}"
                )
                spotifyAlbumRepository.updateTracks(album)
            }
        }
    }

}