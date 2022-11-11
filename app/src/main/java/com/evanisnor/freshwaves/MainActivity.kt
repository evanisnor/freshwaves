package com.evanisnor.freshwaves

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evanisnor.freshwaves.features.albumdetails.AlbumDetailsFragment
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  companion object {
    const val extraAlbumId = "AlbumId"
  }

  @Inject
  lateinit var spotifyAuthorization: SpotifyAuthorization

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    intent.extras?.getInt(extraAlbumId)?.let(this::launchAlbumDetails)
    setContentView(R.layout.main_activity)
  }


  private fun launchAlbumDetails(albumId: Int) {
    val albumDetailsFragment = AlbumDetailsFragment.create(albumId)

    supportFragmentManager
      .beginTransaction()
      .add(android.R.id.content, albumDetailsFragment)
      .addToBackStack(AlbumDetailsFragment.TAG)
      .commit()
  }
}