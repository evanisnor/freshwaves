package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.user.UserProfile
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpotifyAlbumRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindSpotifyAlbumRepository(impl: SpotifyAlbumRepositoryImpl): SpotifyAlbumRepository
}

interface SpotifyAlbumRepository {
  suspend fun getLatestAlbums(limit: Int = 30): Flow<List<Album>>

  suspend fun getLatestAlbumsMissingTracks(): List<Album>

  suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album>

  suspend fun getAlbumWithTracks(albumId: Int): Album

  suspend fun updateAlbums(artist: Artist, userProfile: UserProfile)

  suspend fun updateTracks(album: Album)
}

class SpotifyAlbumRepositoryImpl @Inject constructor(
  private val spotifyNetworkRepository: SpotifyNetworkRepository,
  private val spotifyCacheDao: SpotifyCacheDao,
) : SpotifyAlbumRepository {

  override suspend fun getLatestAlbums(limit: Int): Flow<List<Album>> =
    spotifyCacheDao.readAlbumsWithImages(limit)

  override suspend fun getLatestAlbumsMissingTracks(): List<Album> =
    spotifyCacheDao.readLatestAlbumsMissingTracks(30)

  override suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album> =
    spotifyCacheDao.readAlbumsReleasedAfter(instant)

  override suspend fun getAlbumWithTracks(albumId: Int): Album =
    spotifyCacheDao.readAlbumWithTracks(albumId)

  override suspend fun updateAlbums(artist: Artist, userProfile: UserProfile) {
    spotifyNetworkRepository.artistAlbums(
      artist = artist,
      userProfile = userProfile,
    ).collect { albums ->
      spotifyCacheDao.insertAlbums(albums)
      Timber.d("Inserted ${albums.size} albums for ${artist.name}")
    }
  }

  override suspend fun updateTracks(album: Album) {
    spotifyNetworkRepository.albumTracks(album).collect { tracks ->
      spotifyCacheDao.insertTracks(tracks)
    }
  }
}
