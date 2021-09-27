package com.evanisnor.freshwaves

import android.app.Application
import androidx.room.Room
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.SpotifyCache
import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.repository.SpotifyRepository

class FreshWavesApp : Application() {

    lateinit var spotifyAuthorization: SpotifyAuthorization
    lateinit var spotifyRepository: SpotifyRepository

    override fun onCreate() {
        super.onCreate()

        val userSettings = getSharedPreferences("userSettings", MODE_PRIVATE)

        spotifyAuthorization = SpotifyAuthorization(
            userSettings = userSettings
        )

        spotifyRepository = SpotifyRepository(
            spotifyAuthorization = spotifyAuthorization,
            spotifyAPIService = SpotifyAPIService.create(),
            spotifyCacheDao = Room.databaseBuilder(
                this, SpotifyCache::class.java, "spotifyCache"
            ).build().spotifyCacheDao(),
            userSettings = userSettings
        )
    }

}