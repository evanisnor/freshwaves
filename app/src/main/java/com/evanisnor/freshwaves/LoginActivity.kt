package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization

class LoginActivity : AppCompatActivity() {

    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as FreshWavesApp).let { app ->
            spotifyAuthorization = app.spotifyAuthorization
        }

        spotifyAuthorization.checkLogin(
            loggedIn = {
                startActivity(Intent(this, MainActivity::class.java))
            },
            notLoggedIn = {
                spotifyAuthorization.performLoginAuthorization(
                    activity = this,
                    activityOnSuccess = MainActivity::class,
                    activityOnCancel = LoginActivity::class
                )
            }
        )


    }

}