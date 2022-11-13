package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.user.UserProfile
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

class SpotifyAlbumRepository @Inject constructor(
  private val spotifyNetworkRepository: SpotifyNetworkRepository,
  private val spotifyCacheDao: SpotifyCacheDao,
) {

  suspend fun getLatestAlbums(limit: Int = 30): Flow<List<Album>> =
    spotifyCacheDao.readAlbumsWithImages(limit)

  suspend fun getLatestAlbumsMissingTracks(): List<Album> =
    spotifyCacheDao.readLatestAlbumsMissingTracks(30)

  suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album> =
    spotifyCacheDao.readAlbumsReleasedAfter(instant)

  suspend fun getAlbumWithTracks(albumId: Int): Album =
    spotifyCacheDao.readAlbumWithTracks(albumId)

  suspend fun updateAlbums(artist: Artist, userProfile: UserProfile) {
    spotifyNetworkRepository.artistAlbums(
      artist = artist,
      userProfile = userProfile,
    ).collect { albums ->
      spotifyCacheDao.insertAlbums(albums)
      Timber.d("Inserted ${albums.size} albums for ${artist.name}")
    }
  }

  suspend fun updateTracks(album: Album) {
    spotifyNetworkRepository.albumTracks(album).collect { tracks ->
      spotifyCacheDao.insertTracks(tracks)
    }
  }
}
