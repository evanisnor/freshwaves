package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val spotifyUserRepository: SpotifyUserRepository,
    private val spotifyArtistRepository: SpotifyArtistRepository,
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : Worker(applicationContext, workerParameters) {

    override fun doWork(): Result {

        updateUserProfile {
            Log.i("UpdateWorker", "Updated user profile")
            updateArtists {
                Log.i("UpdateWorker", "Updated top artists")
                updateAlbums { albums ->
                    Log.i("UpdateWorker", "Fetched ${albums.size} albums")
                    albums.forEach { album ->
                        updateTracks(album) {
                            Log.i("UpdateWorker", "Fetched tracks for album ${album.name}")
                        }
                    }
                }
            }
        }

        return Result.success()
    }

    private fun updateUserProfile(onFinished: () -> Unit) {
        spotifyUserRepository.updateUserProfile(
            onFinished = onFinished,
            onError = {
                Log.e("UpdateWorker", "Failed to update user profile: $it")
            }
        )
    }

    private fun updateArtists(onFinished: () -> Unit) {
        spotifyArtistRepository.updateTopArtists(
            onFinished = onFinished,
            onError = {
                Log.e("UpdateWorker", "Failed to update artists: $it")
            }
        )
    }

    private fun updateAlbums(onFinished: (List<Album>) -> Unit) {
        spotifyArtistRepository.getTopArtists().forEach { artist ->
            spotifyAlbumRepository.updateAlbums(
                artist = artist,
                onFinished = onFinished,
                onError = {
                    Log.e("UpdateWorker", "Failed to update albums for $artist: $it")
                }
            )
        }
    }

    private fun updateTracks(album: Album, onFinished: () -> Unit) {
        spotifyAlbumRepository.updateTracks(
            album = album,
            onFinished = onFinished,
            onError = {
                Log.e("UpdateWorker", "Failed to update tracks for for $album: $it")
            }
        )
    }
}