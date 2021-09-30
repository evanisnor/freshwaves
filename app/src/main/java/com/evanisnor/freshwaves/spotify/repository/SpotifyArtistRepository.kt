package com.evanisnor.freshwaves.spotify.repository

import android.content.Context
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import java.util.concurrent.Executors

class SpotifyArtistRepository(
    private val spotifyNetworkRepository: SpotifyNetworkRepository,
    private val spotifyCacheDao: SpotifyCacheDao
) {

    fun getTopArtists() = spotifyCacheDao.readArtists()

    fun updateTopArtists(
        context: Context,
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {

        Executors.newSingleThreadExecutor().execute {
            val pages = 4
            var offset = 0
            for (i in 0..pages) {
                spotifyNetworkRepository.getTopArtists(
                    context = context,
                    offset = offset,
                    onResult = { artists ->
                        offset += artists.size

                        spotifyCacheDao.insertArtists(artists)
                    },
                    onError = onError
                )
            }
            onFinished()
        }

    }
}