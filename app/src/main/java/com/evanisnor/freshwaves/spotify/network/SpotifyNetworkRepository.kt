package com.evanisnor.freshwaves.spotify.network

import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.user.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SpotifyNetworkRepository @Inject constructor(
  private val spotifyAuthorization: SpotifyAuthorization,
  private val spotifyAPIService: SpotifyAPIService,
) {

  suspend fun userProfile(): UserProfile = withContext(Dispatchers.IO) {
    val bearerToken = getBearerToken()
    spotifyAPIService.getUserProfile(bearerToken).mapToUserProfile()
  }

  suspend fun topArtists(limit: Int, offset: Int): List<Artist> = withContext(Dispatchers.IO) {
    val bearerToken = getBearerToken()
    val topArtists = spotifyAPIService.getTopArtists(
      accessToken = bearerToken,
      limit = limit,
      offset = offset,
    )
    topArtists.items.mapToEntity()
  }

  suspend fun artistAlbums(
    artist: Artist,
    userProfile: UserProfile,
  ) = flow {
    val bearerToken = getBearerToken()
    val albums = spotifyAPIService.getArtistAlbums(
      accessToken = bearerToken,
      artistId = artist.id,
      market = userProfile.country,
    )
    emit(albums.items.mapToEntity(artist.id))
  }.flowOn(Dispatchers.IO)

  suspend fun albumTracks(album: Album) = flow {
    val bearerToken = getBearerToken()
    val tracks = spotifyAPIService.getAlbumTracks(
      accessToken = bearerToken,
      albumId = album.spotifyId,
    )
    emit(tracks.items.mapToEntities(album.id))
  }.flowOn(Dispatchers.IO)

  private suspend fun getBearerToken(): String = try {
    spotifyAuthorization.getAuthorizationHeader()
  } catch (error: Throwable) {
    Timber.w(error)
    throw error
  }
}
