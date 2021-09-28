package com.evanisnor.freshwaves.spotify.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.enqueue
import java.util.concurrent.Executors

class SpotifyRepository(
    private val spotifyAuthorization: SpotifyAuthorization,
    private val spotifyAPIService: SpotifyAPIService,
    private val spotifyCacheDao: SpotifyCacheDao,
    private val userSettings: SharedPreferences
) {

    // region with updater

    fun updateUserProfile(
        context: Context,
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        authorizedAction(
            context = context,
            withFreshAccessToken = { accessToken ->

                Executors.newSingleThreadExecutor().execute {

                    spotifyAPIService.getUserProfile(accessToken).enqueue(
                        onResult = {
                            val userProfile = it.mapToUserProfile()
                            userSettings.edit()
                                .putString("id", userProfile.id)
                                .putString("name", userProfile.name)
                                .putString("email", userProfile.email)
                                .putString("country", userProfile.country)
                                .apply()
                            onFinished()
                        },
                        onError = onError
                    )
                }
            }
        )
    }


    fun updateTopArtists(
        context: Context,
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        authorizedAction(
            context = context,
            withFreshAccessToken = { accessToken ->

                Executors.newSingleThreadExecutor().execute {
                    val pages = 4
                    var offset = 0
                    for (i in 0..pages) {
                        updateArtist(
                            accessToken = accessToken,
                            offset = offset,
                            finishedPage = { numArtists ->
                                offset += numArtists
                            },
                            onError = onError
                        )
                    }
                    onFinished()
                }

            }
        )
    }

    private fun updateArtist(
        accessToken: String,
        offset: Int,
        finishedPage: (Int) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        spotifyAPIService.getTopArtists(accessToken, offset = offset).enqueue(
            onResult = {

                Executors.newSingleThreadExecutor().execute {
                    val artists = it.items.mapToEntity()
                    spotifyCacheDao.insertArtists(artists)
                    finishedPage(it.items.size)
                }

            },
            onError = onError
        )
    }

    fun getTopArtists() = spotifyCacheDao.readArtists()

    fun updateAlbums(
        artist: Artist,
        context: Context,
        onFinished: (List<Album>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        authorizedAction(
            context = context,
            withFreshAccessToken = { accessToken ->

                Executors.newSingleThreadExecutor().execute {
                    spotifyAPIService.getArtistAlbums(
                        accessToken = accessToken,
                        artistId = artist.id,
                        market = userSettings.getString("country", "") ?: ""
                    ).enqueue(
                        onResult = {

                            Executors.newSingleThreadExecutor().execute {
                                val albums = it.items.mapToEntity(artist)
                                spotifyCacheDao.insertAlbums(albums)
                                onFinished(albums)
                            }
                        },
                        onError = onError
                    )
                }

            }
        )
    }

    fun updateTracks(
        album: Album,
        context: Context,
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        authorizedAction(
            context = context,
            withFreshAccessToken = { accessToken ->
                spotifyAPIService.getAlbumTracks(
                    accessToken = accessToken,
                    albumId = album.spotifyId
                ).enqueue(
                    onResult = {

                        Executors.newSingleThreadExecutor().execute {
                            val tracks = it.items.mapToEntities(album.id)
                            spotifyCacheDao.insertTracks(tracks)
                            onFinished()
                        }
                    },
                    onError = onError
                )
            }
        )
    }

    fun getLatestAlbums() = spotifyCacheDao.readAlbumsWithLimit(30)

    fun getAlbumWithTracks(albumId: Int) = spotifyCacheDao.readAlbumWithTracks(albumId)

    // endregion

    private fun authorizedAction(
        context: Context,
        withFreshAccessToken: (String) -> Unit
    ) {
        spotifyAuthorization.refreshToken(context,
            withAccessToken = withFreshAccessToken,
            withError = {
                Log.e("SpotifyRepository", "Authorization error: ${it.toJsonString()}")
            })
    }


}