package com.evanisnor.freshwaves.spotify.network

import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import com.evanisnor.freshwaves.user.UserProfile
import java.util.concurrent.Executors
import javax.inject.Inject

class SpotifyNetworkRepository @Inject constructor(
    private val spotifyAuthorization: SpotifyAuthorization,
    private val spotifyAPIService: SpotifyAPIService
) {

    fun getUserProfile(
        onResult: (UserProfile) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.useBearerToken(
            withBearerToken = { accessToken ->

                spotifyAPIService.getUserProfile(accessToken).enqueue(
                    onResult = {
                        onResult(it.mapToUserProfile())
                    },
                    onError = onError
                )
            }
        )
    }


    fun getTopArtists(
        offset: Int,
        onResult: (List<Artist>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.useBearerToken(
            withBearerToken = { accessToken ->

                spotifyAPIService.getTopArtists(accessToken, offset = offset).enqueue(
                    onResult = {

                        Executors.newSingleThreadExecutor().execute {
                            onResult(it.items.mapToEntity())
                        }

                    },
                    onError = onError
                )
            }
        )
    }

    fun getArtistAlbums(
        artist: Artist,
        market: String,
        onResult: (List<Album>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.useBearerToken(
            withBearerToken = { accessToken ->

                Executors.newSingleThreadExecutor().execute {
                    spotifyAPIService.getArtistAlbums(
                        accessToken = accessToken,
                        artistId = artist.id,
                        market = market
                    ).enqueue(
                        onResult = {

                            Executors.newSingleThreadExecutor().execute {
                                onResult(it.items.mapToEntity(artist))
                            }
                        },
                        onError = onError
                    )
                }

            }
        )
    }

    fun getAlbumTracks(
        album: Album,
        onResult: (List<Track>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.useBearerToken(
            withBearerToken = { accessToken ->

                spotifyAPIService.getAlbumTracks(
                    accessToken = accessToken,
                    albumId = album.spotifyId
                ).enqueue(
                    onResult = {

                        Executors.newSingleThreadExecutor().execute {
                            onResult(it.items.mapToEntities(album.id))
                        }

                    },
                    onError = onError
                )
            }
        )
    }
}