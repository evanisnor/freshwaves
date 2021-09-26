package com.evanisnor.freshwaves

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository
import com.evanisnor.freshwaves.features.updater.UpdateWorker
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    lateinit var spotifyRepository: SpotifyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as FreshWavesApp).let { app ->
            spotifyRepository = app.spotifyRepository
        }

        WorkManager.getInstance(this)
            .enqueue(OneTimeWorkRequestBuilder<UpdateWorker>().build())
    }

    fun onClickFetch(v: View) {
        Executors.newSingleThreadExecutor().execute {
            Log.i(
                "MainActivity",
                "\n---------------------------\nLATEST ALBUMS\n---------------------------"
            )
            spotifyRepository.getLatestAlbums().forEach {
                Log.i(
                    "MainActivity",
                    "${it.name} by ${it.artist?.name ?: "UNKNOWN"}\t\tRelease Date: ${it.releaseDate}"
                )
            }
        }
    }
}