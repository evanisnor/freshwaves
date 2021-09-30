package com.evanisnor.freshwaves.fakes

import android.content.Context
import androidx.room.Room
import com.evanisnor.freshwaves.spotify.cache.SpotifyCache
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SpotifyCacheModule::class]
)
object FakeSpotifyCacheModule {

    @Provides
    @Singleton
    fun spotifyCacheDao(@ApplicationContext context: Context): SpotifyCacheDao =
        Room.inMemoryDatabaseBuilder(
            context,
            SpotifyCache::class.java
        ).build().spotifyCacheDao()

}