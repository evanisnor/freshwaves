package com.evanisnor.freshwaves.spotify.network

import android.content.Context
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import com.evanisnor.freshwaves.user.UserProfile
import java.util.concurrent.Executors

class SpotifyNetworkRepository(
    private val spotifyAuthorization: SpotifyAuthorization,
    private val spotifyAPIService: SpotifyAPIService
) {

    fun getUserProfile(
        context: Context,
        onResult: (UserProfile) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.provideAccessToken(
            context = context,
            withAccessToken = { accessToken ->

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
        context: Context,
        offset: Int,
        onResult: (List<Artist>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.provideAccessToken(
            context = context,
            withAccessToken = { accessToken ->

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
        context: Context,
        onResult: (List<Album>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.provideAccessToken(
            context = context,
            withAccessToken = { accessToken ->

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
        context: Context,
        onResult: (List<Track>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        spotifyAuthorization.provideAccessToken(
            context = context,
            withAccessToken = { accessToken ->

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