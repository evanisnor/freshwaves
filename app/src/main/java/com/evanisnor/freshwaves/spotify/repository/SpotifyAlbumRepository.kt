package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import java.util.concurrent.Executors
import javax.inject.Inject

class SpotifyAlbumRepository @Inject constructor(
    private val spotifyUserRepository: SpotifyUserRepository,
    private val spotifyNetworkRepository: SpotifyNetworkRepository,
    private val spotifyCacheDao: SpotifyCacheDao
) {

    fun getLatestAlbums(onResult: (List<Album>) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            onResult(spotifyCacheDao.readAlbumsWithLimit(30))
        }
    }

    fun getAlbumWithTracks(albumId: Int, onResult: (Album) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            onResult(spotifyCacheDao.readAlbumWithTracks(albumId))
        }
    }

    fun updateAlbums(
        artist: Artist,
        onFinished: (List<Album>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Executors.newSingleThreadExecutor().execute {

            spotifyNetworkRepository.getArtistAlbums(
                artist = artist,
                market = spotifyUserRepository.getUserMarket(),
                onResult = { albums ->

                    Executors.newSingleThreadExecutor().execute {
                        spotifyCacheDao.insertAlbums(albums)
                        onFinished(albums)
                    }
                },
                onError = onError
            )

        }
    }

    fun updateTracks(
        album: Album,
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyNetworkRepository.getAlbumTracks(
            album = album,
            onResult = { tracks ->

                Executors.newSingleThreadExecutor().execute {
                    spotifyCacheDao.insertTracks(tracks)
                    onFinished()
                }

            },
            onError = onError
        )
    }
}