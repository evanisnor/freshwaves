package com.evanisnor.freshwaves.spotify

import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import com.evanisnor.freshwaves.user.UserProfile
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SpotifyRepositoryImpl @Inject constructor(
  private val spotifyUserRepository: SpotifyUserRepository,
  private val spotifyArtistRepository: SpotifyArtistRepository,
  private val spotifyAlbumRepository: SpotifyAlbumRepository,
) : SpotifyRepository {

  override suspend fun userProfile(): UserProfile = spotifyUserRepository.userProfile()

  override suspend fun getTopArtists(): List<Artist> = spotifyArtistRepository.getTopArtists()

  override suspend fun updateTopArtists(numberOfArtists: Int, artistsPerPage: Int) =
    spotifyArtistRepository.updateTopArtists(numberOfArtists, artistsPerPage)

  override suspend fun getLatestAlbums(): Flow<List<Album>> =
    spotifyAlbumRepository.getLatestAlbums()

  override suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album> =
    spotifyAlbumRepository.getAlbumsReleasedAfter(instant)

  override suspend fun getLatestAlbumsMissingTracks(): List<Album> =
    spotifyAlbumRepository.getLatestAlbumsMissingTracks()

  override suspend fun updateAlbums(artist: Artist, userProfile: UserProfile) =
    spotifyAlbumRepository.updateAlbums(artist, userProfile)

  override suspend fun updateTracks(album: Album) = spotifyAlbumRepository.updateTracks(album)
}