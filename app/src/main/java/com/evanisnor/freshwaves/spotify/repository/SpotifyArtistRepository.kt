package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Module
@InstallIn(SingletonComponent::class)
abstract class SpotifyArtistRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindSpotifyArtistRepository(impl: SpotifyArtistRepositoryImpl): SpotifyArtistRepository
}

interface SpotifyArtistRepository {
  suspend fun getTopArtists(): List<Artist>
  suspend fun updateTopArtists(numberOfArtists: Int, artistsPerPage: Int = 30)
}

class SpotifyArtistRepositoryImpl @Inject constructor(
  private val spotifyNetworkRepository: SpotifyNetworkRepository,
  private val spotifyCacheDao: SpotifyCacheDao,
) : SpotifyArtistRepository {

  override suspend fun getTopArtists(): List<Artist> = spotifyCacheDao.readArtists()

  override suspend fun updateTopArtists(numberOfArtists: Int, artistsPerPage: Int) {
    val pages = ceil(numberOfArtists / artistsPerPage.toFloat()).toInt()
    var offset = 0

    for (i in 0 until pages) {
      val artists = spotifyNetworkRepository.topArtists(artistsPerPage, offset)
      Timber.d("Fetched ${artists.size} artists")
      spotifyCacheDao.insertArtists(artists)
      Timber.d("Inserted ${artists.size} artists")
      offset += artists.size
    }
  }
}
