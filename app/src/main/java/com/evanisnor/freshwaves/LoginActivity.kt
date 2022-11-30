package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

  @Inject
  lateinit var spotifyAuthorization: SpotifyAuthorization

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (spotifyAuthorization.isAuthorized) {
      proceedToFreshAlbums()
    } else {
      setContentView(R.layout.login_activity)
    }
  }

  private fun proceedToFreshAlbums() {
    startActivity(
      Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      },
    )
  }
}
