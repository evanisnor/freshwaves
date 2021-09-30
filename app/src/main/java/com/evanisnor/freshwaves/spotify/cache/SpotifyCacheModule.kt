package com.evanisnor.freshwaves.spotify.cache

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SpotifyCacheModule {

    @Provides
    fun spotifyCacheDao(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        SpotifyCache::class.java,
        "spotifyCache"
    ).build().spotifyCacheDao()

}