package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.FreshWavesApp
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album

class UpdateWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters
) : Worker(applicationContext, workerParameters) {

    private val spotifyRepository = (applicationContext as FreshWavesApp).spotifyRepository

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
        spotifyRepository.updateUserProfile(
            context = applicationContext,
            onFinished = onFinished,
            onError = {
                Log.e("UpdateWorker", "Failed to update user profile: $it")
            }
        )
    }

    private fun updateArtists(onFinished: () -> Unit) {
        spotifyRepository.updateTopArtists(
            context = applicationContext,
            onFinished = onFinished,
            onError = {
                Log.e("UpdateWorker", "Failed to update artists: $it")
            }
        )
    }

    private fun updateAlbums(onFinished: (List<Album>) -> Unit) {
        spotifyRepository.getTopArtists().forEach { artist ->
            spotifyRepository.updateAlbums(
                artist = artist,
                context = applicationContext,
                onFinished = onFinished,
                onError = {
                    Log.e("UpdateWorker", "Failed to update albums for $artist: $it")
                }
            )
        }
    }

    private fun updateTracks(album: Album, onFinished: () -> Unit) {
        spotifyRepository.updateTracks(
            album = album,
            context = applicationContext,
            onFinished = onFinished,
            onError = {
                Log.e("UpdateWorker", "Failed to update tracks for for $album: $it")
            }
        )
    }
}