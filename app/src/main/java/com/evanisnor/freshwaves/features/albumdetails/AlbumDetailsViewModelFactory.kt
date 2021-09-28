package com.evanisnor.freshwaves.features.albumdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository

class AlbumDetailsViewModelFactory(
    private val spotifyRepository: SpotifyRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == AlbumDetailsViewModel::class.java) {
            return AlbumDetailsViewModel(spotifyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel type $modelClass")
    }

}