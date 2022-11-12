package com.evanisnor.freshwaves.spotify.api

import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.user.UserProfile
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface SpotifyRepository {

  suspend fun update()

  suspend fun getLatestAlbums(limit: Int = 30): Flow<List<Album>>

  suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album>

  suspend fun getAlbumWithTracks(albumId: Int): Album

  // TODO remove these:
  suspend fun userProfile(): UserProfile

  suspend fun getTopArtists(): List<Artist>

  suspend fun updateTopArtists(numberOfArtists: Int, artistsPerPage: Int = 30)

  suspend fun getLatestAlbumsMissingTracks(): List<Album>

  suspend fun updateAlbums(artist: Artist, userProfile: UserProfile)

  suspend fun updateTracks(album: Album)
}