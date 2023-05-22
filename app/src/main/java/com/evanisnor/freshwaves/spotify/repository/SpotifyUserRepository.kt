package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkDataSource
import com.evanisnor.freshwaves.user.UserProfile
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpotifyUserRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindSpotifyUserRepository(impl: SpotifyUserRepositoryImpl): SpotifyUserRepository
}

interface SpotifyUserRepository {
  suspend fun userProfile(): UserProfile
}

class SpotifyUserRepositoryImpl @Inject constructor(
  private val spotifyNetworkDataSource: SpotifyNetworkDataSource,
) : SpotifyUserRepository {

  override suspend fun userProfile(): UserProfile = spotifyNetworkDataSource.userProfile()
}
