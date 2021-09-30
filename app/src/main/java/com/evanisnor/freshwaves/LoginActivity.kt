package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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