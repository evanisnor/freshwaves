package com.evanisnor.freshwaves.features.albumdetails

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository
import java.util.concurrent.Executors

class AlbumDetailsViewModel(
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {

    fun getAlbumWithTracks(albumId: Int, onResult: (Album) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            onResult(spotifyRepository.getAlbumWithTracks(albumId))
        }
    }

}