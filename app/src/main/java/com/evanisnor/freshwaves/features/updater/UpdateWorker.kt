package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.evanisnor.freshwaves.FreshWavesApp

class UpdateWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters
) : Worker(applicationContext, workerParameters) {

    private val spotifyRepository = (applicationContext as FreshWavesApp).spotifyRepository

    override fun doWork(): Result {

        updateUserProfile {
            updateArtists {
                updateAlbums {
                    // TODO Update Tracks
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

    private fun updateAlbums(onFinished: () -> Unit) {
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
}