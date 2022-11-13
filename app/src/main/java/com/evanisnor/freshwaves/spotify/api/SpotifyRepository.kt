package com.evanisnor.freshwaves.spotify.api

import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface SpotifyRepository {

  suspend fun update()

  suspend fun getLatestAlbums(limit: Int = 30): Flow<List<Album>>

  suspend fun getAlbumsReleasedAfter(instant: Instant): List<Album>

  suspend fun getAlbumWithTracks(albumId: Int): Album
}
