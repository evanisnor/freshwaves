package com.evanisnor.freshwaves.features.freshalbums

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository
import java.util.concurrent.Executors

class FreshAlbumsViewModel(
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {

    fun getLatestAlbums(onResult: (List<Album>) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            onResult(spotifyRepository.getLatestAlbums())
        }
    }

}