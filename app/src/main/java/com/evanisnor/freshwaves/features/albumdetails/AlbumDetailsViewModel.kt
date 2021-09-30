package com.evanisnor.freshwaves.features.albumdetails

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository

class AlbumDetailsViewModel(
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    fun getAlbumWithTracks(albumId: Int, onResult: (Album) -> Unit) =
        spotifyAlbumRepository.getAlbumWithTracks(albumId, onResult)

}