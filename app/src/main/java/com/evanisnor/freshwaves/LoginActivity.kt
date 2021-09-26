package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository

class LoginActivity : AppCompatActivity() {

    lateinit var spotifyRepository: SpotifyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as FreshWavesApp).let { app ->
            spotifyRepository = app.spotifyRepository
        }

        spotifyRepository.authorizeIfNeeded(this) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}