package com.evanisnor.freshwaves.spotify.network

import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.user.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Data Source for interacting with the remote Spotify API
 */
class SpotifyNetworkDataSource @Inject constructor(
  private val spotifyAuthorization: SpotifyAuthorization,
  private val spotifyAPIService: SpotifyAPIService,
) {

  class BearerTokenRefreshError(attempts: Int, cause: Throwable?) : Error(
    "Failed to refresh bearer token after $attempts attempts",
    cause,
  )

  suspend fun userProfile(): PrivateUserObject = withContext(Dispatchers.IO) {
    val bearerToken = getBearerToken()
    spotifyAPIService.getUserProfile(bearerToken)
  }

  suspend fun topArtists(limit: Int, offset: Int): Collection<ArtistObject> = withContext(Dispatchers.IO) {
    val bearerToken = getBearerToken()
    val topArtists = spotifyAPIService.getTopArtists(
      accessToken = bearerToken,
      limit = limit,
      offset = offset,
    )
    topArtists.items
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
    emit(albums.items)
  }.flowOn(Dispatchers.IO)

  suspend fun albumTracks(album: Album) = flow {
    val bearerToken = getBearerToken()
    val tracks = spotifyAPIService.getAlbumTracks(
      accessToken = bearerToken,
      albumId = album.spotifyId,
    )
    emit(tracks.items)
  }.flowOn(Dispatchers.IO)

  private suspend fun getBearerToken(attempt: Int = 0): String = try {
    spotifyAuthorization.getAuthorizationHeader()
  } catch (error: Throwable) {
    if (attempt >= BEARER_TOKEN_RETRY_MAX_ATTEMPTS) {
      throw BearerTokenRefreshError(attempt, error)
    }
    delay(BEARER_TOKEN_RETRY_DELAY_MS)
    getBearerToken(attempt + 1)
  }

  companion object {
    private const val BEARER_TOKEN_RETRY_DELAY_MS = 3000L
    private const val BEARER_TOKEN_RETRY_MAX_ATTEMPTS = 3
  }
}
