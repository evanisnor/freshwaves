package com.evanisnor.freshwaves.features.albumdetails

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    fun getAlbumWithTracks(albumId: Int, onResult: (Album) -> Unit) =
        spotifyAlbumRepository.getAlbumWithTracks(albumId, onResult)

}