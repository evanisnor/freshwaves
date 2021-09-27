package com.evanisnor.freshwaves

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.evanisnor.freshwaves.features.updater.UpdateWorker
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository

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
}