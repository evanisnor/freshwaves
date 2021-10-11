package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import javax.inject.Inject

class SpotifyArtistRepository @Inject constructor(
    private val spotifyNetworkRepository: SpotifyNetworkRepository,
    private val spotifyCacheDao: SpotifyCacheDao
) {

    fun getTopArtists() = spotifyCacheDao.readArtists()

    fun updateTopArtists(
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {

        Executors.newSingleThreadExecutor().execute {
            val pages = 4
            var offset = 0
            for (i in 0..pages) {
                val latch = CountDownLatch(1)
                spotifyNetworkRepository.getTopArtists(
                    offset = offset,
                    onResult = { artists ->
                        offset += artists.size

                        spotifyCacheDao.insertArtists(artists)
                        latch.countDown()
                    },
                    onError = {
                        latch.countDown()
                        onError(it)
                    }
                )
                latch.await()
            }
            onFinished()
        }

    }
}