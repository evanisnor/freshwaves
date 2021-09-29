package com.evanisnor.freshwaves

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.evanisnor.freshwaves.features.updater.UpdateWorker
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization

class MainActivity : AppCompatActivity() {

    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(application as FreshWavesApp) {
            this@MainActivity.spotifyAuthorization = spotifyAuthorization
        }

        spotifyAuthorization.authorize(
            activity = this,
            onAuthorized = {
//                WorkManager.getInstance(this)
//                    .enqueue(OneTimeWorkRequestBuilder<UpdateWorker>().build())
            },
            onAuthorizationError = {}
        )
    }
}