package com.evanisnor.freshwaves.features.freshalbums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evanisnor.freshwaves.features.updater.UpdaterRepository
import com.evanisnor.freshwaves.features.updater.UpdaterState
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FreshAlbumsViewModel @Inject constructor(
    updaterRepository: UpdaterRepository,
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    val updaterState: StateFlow<UpdaterState> = updaterRepository.state

    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    init {
        viewModelScope.launch {
            spotifyAlbumRepository.getLatestAlbums().collect(_albums::emit)
        }
    }

}