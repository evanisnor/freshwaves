package com.evanisnor.freshwaves.spotify

import android.util.Log
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(SingletonComponent::class)
abstract class SpotifyRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindSpotifyRepository(impl: SpotifyRepositoryImpl): SpotifyRepository
}

class SpotifyRepositoryImpl @Inject constructor(
  private val spotifyUserRepository: SpotifyUserRepository,
  private val spotifyArtistRepository: SpotifyArtistRepository,
  private val spotifyAlbumRepository: SpotifyAlbumRepository,
) : SpotifyRepository {

  override suspend fun update() {
    Log.i("UpdateWorker", "Fetching user profile")
    val userProfile = spotifyUserRepository.userProfile()

    Log.i("UpdateWorker", "Fetching top artists")
    spotifyArtistRepository.updateTopArtists(120)

    Log.i("UpdateWorker", "Fetching albums...")
    spotifyArtistRepository.getTopArtists().let { artists ->
      artists.forEach { artist ->
        Log.i("UpdateWorker", "Fetching albums for ${artist.name}")
        spotifyAlbumRepository.updateAlbums(artist, userProfile)
      }
    }

    Log.i("UpdateWorker", "Fetching missing tracks...")
    spotifyAlbumRepository.getLatestAlbumsMissingTracks().let { albums ->
      albums.forEach { album ->
        Log.i(
          "UpdateWorker",
          "Fetching tracks for ${album.artist?.name ?: "???"} - ${album.name}"
        )
        spotifyAlbumRepository.updateTracks(album)
      }
    }
  }

  override suspend fun getLatestAlbums(limit: Int): Flow<List<Album>> =
    spotifyAlbumRepository.getLatestAlbums(limit)

  override suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album> =
    spotifyAlbumRepository.getAlbumsReleasedAfter(instant)

  override suspend fun getAlbumWithTracks(albumId: Int): Album =
    spotifyAlbumRepository.getAlbumWithTracks(albumId)
}