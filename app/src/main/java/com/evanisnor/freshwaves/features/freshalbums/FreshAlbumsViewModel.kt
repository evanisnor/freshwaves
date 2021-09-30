package com.evanisnor.freshwaves.features.freshalbums

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FreshAlbumsViewModel @Inject constructor(
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    fun getLatestAlbums(onResult: (List<Album>) -> Unit) =
        spotifyAlbumRepository.getLatestAlbums(onResult)

}