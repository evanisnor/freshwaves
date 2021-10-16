package com.evanisnor.freshwaves

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyAuthorization: SpotifyAuthorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            try {
                spotifyAuthorization.confirmAuthorization(this@MainActivity)
            } catch (error: Throwable) {
                Log.w("MainActivity", "Failed to authorize user: $error")
            }
        }
    }
}