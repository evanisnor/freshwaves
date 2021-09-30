package com.evanisnor.freshwaves.features.freshalbums

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository

class FreshAlbumsViewModel(
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    fun getLatestAlbums(onResult: (List<Album>) -> Unit) =
        spotifyAlbumRepository.getLatestAlbums(onResult)

}