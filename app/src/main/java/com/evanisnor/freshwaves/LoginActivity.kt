package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            spotifyAuthorization.checkLogin(
                loggedIn = {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                },
                notLoggedIn = {
                    spotifyAuthorization.performLoginAuthorization(
                        activity = this@LoginActivity,
                        activityOnSuccess = MainActivity::class,
                        activityOnCancel = LoginActivity::class
                    )
                }
            )
        }

    }

}