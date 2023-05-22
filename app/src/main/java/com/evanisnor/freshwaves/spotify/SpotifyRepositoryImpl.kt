package com.evanisnor.freshwaves.spotify

import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.cache.SpotifyCache
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import com.evanisnor.freshwaves.user.UserStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpotifyRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindSpotifyRepository(impl: SpotifyRepositoryImpl): SpotifyRepository
}

class SpotifyRepositoryImpl @Inject constructor(
  private val spotifyCacheDatabase: SpotifyCache,
  private val userStateRepository: UserStateRepository,
  private val spotifyUserRepository: SpotifyUserRepository,
  private val spotifyArtistRepository: SpotifyArtistRepository,
  private val spotifyAlbumRepository: SpotifyAlbumRepository,
) : SpotifyRepository,
  SpotifyAlbumRepository by spotifyAlbumRepository,
  SpotifyArtistRepository by spotifyArtistRepository,
  SpotifyUserRepository by spotifyUserRepository {

  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  init {
    scope.launch {
      userStateRepository.currentState.collect {
        if (it == UserStateRepository.State.NoUser) {
          spotifyCacheDatabase.clearAllTables()
        }
      }
    }
  }
}
