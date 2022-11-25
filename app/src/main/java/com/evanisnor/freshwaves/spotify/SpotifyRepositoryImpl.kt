package com.evanisnor.freshwaves.spotify

import com.evanisnor.freshwaves.backend.BackendAPIRepository
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
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
  private val spotifyUserRepository: SpotifyUserRepository,
  private val spotifyArtistRepository: SpotifyArtistRepository,
  private val spotifyAlbumRepository: SpotifyAlbumRepository,
  private val backendAPIRepository: BackendAPIRepository,
) : SpotifyRepository {

  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  override suspend fun update() = withContext(scope.coroutineContext) {
    Timber.d("Fetching user profile")
    val userProfile = spotifyUserRepository.userProfile()

    Timber.d("Fetching top artists")
    spotifyArtistRepository.updateTopArtists(120)
    val artists = spotifyArtistRepository.getTopArtists()

    Timber.d("Reporting top artists...")
    // Send this asynchronously so we don't interrupt the UI with lag or errors
    scope.launch {
      backendAPIRepository.reportFavouriteArtists(artists.map { it.name })
    }

    Timber.d("Fetching albums...")
    artists.forEach { artist ->
      Timber.d("Fetching albums for ${artist.name}")
      spotifyAlbumRepository.updateAlbums(artist, userProfile)
    }

    Timber.d("Fetching missing tracks...")
    spotifyAlbumRepository.getLatestAlbumsMissingTracks().let { albums ->
      albums.forEach { album ->
        Timber.d("Fetching tracks for ${album.artist?.name ?: "???"} - ${album.name}")
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
