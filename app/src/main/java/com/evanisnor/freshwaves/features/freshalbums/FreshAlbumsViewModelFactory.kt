package com.evanisnor.freshwaves.features.freshalbums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository

class FreshAlbumsViewModelFactory(
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == FreshAlbumsViewModel::class.java) {
            return FreshAlbumsViewModel(spotifyAlbumRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel type $modelClass")
    }

}