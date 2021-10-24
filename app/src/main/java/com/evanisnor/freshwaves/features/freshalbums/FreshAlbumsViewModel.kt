package com.evanisnor.freshwaves.features.freshalbums

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.evanisnor.freshwaves.features.updater.UpdaterBootstrapper
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
    private val spotifyAlbumRepository: SpotifyAlbumRepository
) : ViewModel() {

    private val _updaterState: MutableStateFlow<UpdaterState> =
        MutableStateFlow(UpdaterState.Idle)
    val updaterState: StateFlow<UpdaterState> = _updaterState

    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    init {
        viewModelScope.launch {
            spotifyAlbumRepository.getLatestAlbums().collect(_albums::emit)
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val result =
                    it.getSerializableExtra(UpdaterBootstrapper.updaterStatusExtra) as UpdaterState

                viewModelScope.launch {
                    _updaterState.emit(result)
                }
            }
        }
    }

    fun registerForUpdaterStatus(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(
                broadcastReceiver,
                IntentFilter(UpdaterBootstrapper.updaterStatusIntent)
            )
    }

}