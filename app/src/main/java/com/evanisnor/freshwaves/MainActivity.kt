package com.evanisnor.freshwaves

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.evanisnor.freshwaves.features.freshalbums.FreshAlbumsFragment
import com.evanisnor.freshwaves.features.updater.UpdateWorker
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spotifyAuthorization.authorize(
            activity = this,
            onAuthorized = {
                WorkManager.getInstance(this)
                    .enqueue(OneTimeWorkRequestBuilder<UpdateWorker>().build())
            },
            onAuthorizationError = {}
        )
    }
}