package com.evanisnor.freshwaves.spotify.cache

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyCacheModule {

  @Provides
  fun spotifyCacheDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
    context,
    SpotifyCache::class.java,
    "spotifyCache",
  ).build()

  @Provides
  @Singleton
  fun spotifyCacheDao(database: SpotifyCache) = database.spotifyCacheDao()
}
