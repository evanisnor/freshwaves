package com.evanisnor.freshwaves.spotify.api

import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.user.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.time.Instant

class FakeSpotifyRepository(
  var albums: MutableList<Album> = mutableListOf(),
) : SpotifyRepository {

  override suspend fun getLatestAlbums(limit: Int): Flow<List<Album>> =
    listOf(albums.subList(0, limit)).asFlow()

  override suspend fun getLatestAlbumsMissingTracks(): List<Album> {
    TODO("Not yet implemented")
  }

  override suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album> =
    albums.filter { it.releaseDate.isAfter(instant) }

  override suspend fun getAlbumWithTracks(albumId: Int): Album =
    checkNotNull(albums.find { it.id == albumId }) { "Test data does not include album ID $albumId" }

  override suspend fun updateAlbums(artist: Artist, userProfile: UserProfile) {
    TODO("Not yet implemented")
  }

  override suspend fun updateTracks(album: Album) {
    TODO("Not yet implemented")
  }

  override suspend fun getTopArtists(): List<Artist> {
    TODO("Not yet implemented")
  }

  override suspend fun updateTopArtists(numberOfArtists: Int, artistsPerPage: Int) {
    TODO("Not yet implemented")
  }

  override suspend fun userProfile(): UserProfile {
    TODO("Not yet implemented")
  }
}
