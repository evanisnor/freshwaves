package com.evanisnor.freshwaves.features.albumdetails

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
  private val spotifyRepository: SpotifyRepository,
) : ViewModel() {

  suspend fun getAlbumWithTracks(albumId: Int) =
    spotifyRepository.getAlbumWithTracks(albumId)

}