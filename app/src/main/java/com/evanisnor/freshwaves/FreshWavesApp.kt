package com.evanisnor.freshwaves

import android.app.Application
import androidx.room.Room
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.SpotifyCache
import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository

class FreshWavesApp : Application() {

    lateinit var spotifyAuthorization: SpotifyAuthorization
    lateinit var spotifyUserRepository: SpotifyUserRepository
    lateinit var spotifyArtistRepository: SpotifyArtistRepository
    lateinit var spotifyAlbumRepository: SpotifyAlbumRepository

    override fun onCreate() {
        super.onCreate()

        val userSettings = getSharedPreferences("userSettings", MODE_PRIVATE)

        val spotifyAPIService = SpotifyAPIService.create()
        val spotifyCacheDao = Room.databaseBuilder(
            this, SpotifyCache::class.java, "spotifyCache"
        ).build().spotifyCacheDao()

        spotifyAuthorization = SpotifyAuthorization(
            userSettings = userSettings
        )

        val spotifyNetworkRepository = SpotifyNetworkRepository(
            spotifyAuthorization = spotifyAuthorization,
            spotifyAPIService = spotifyAPIService
        )

        spotifyUserRepository = SpotifyUserRepository(
            spotifyNetworkRepository = spotifyNetworkRepository,
            userSettings = userSettings
        )

        spotifyArtistRepository = SpotifyArtistRepository(
            spotifyNetworkRepository = spotifyNetworkRepository,
            spotifyCacheDao = spotifyCacheDao
        )

        spotifyAlbumRepository = SpotifyAlbumRepository(
            spotifyUserRepository = spotifyUserRepository,
            spotifyNetworkRepository = spotifyNetworkRepository,
            spotifyCacheDao = spotifyCacheDao
        )
    }

}