package com.evanisnor.freshwaves.features.albumdetails

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    suspend fun getAlbumWithTracks(albumId: Int) =
        spotifyAlbumRepository.getAlbumWithTracks(albumId)

}