package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            checkLogin()
        }
    }

    private suspend fun checkLogin() {
        if (spotifyAuthorization.isAuthorized) {
            proceed()
        } else {
            when(spotifyAuthorization.authorize(this)) {
                is SpotifyAuthorization.Response.Success -> proceed()
                is SpotifyAuthorization.Response.Failure -> backToLogin()
            }
        }
    }

    private fun proceed() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
    }

    private fun backToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
    }
}