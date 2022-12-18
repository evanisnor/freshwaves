package com.evanisnor.freshwaves.spotify.network

import com.evanisnor.freshwaves.ext.wrapHttpException
import com.evanisnor.freshwaves.spotify.network.model.AlbumObject
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PagingObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.spotify.network.model.TrackObject

/**
 * Dealing with this: https://github.com/square/retrofit/issues/3474
 * Wrapping HTTP exception in a throwable so we can see useful error information.
 */
class SpotifyAPIServiceErrorWrappingDelegate(
  private val spotifyAPIService: SpotifyAPIService,
) : SpotifyAPIService by spotifyAPIService {

  private suspend fun <T> wrapForErrorHandling(call: suspend () -> T) = try {
    call()
  } catch (e: Throwable) {
    throw e.wrapHttpException()
  }

  override suspend fun getUserProfile(accessToken: String): PrivateUserObject =
    wrapForErrorHandling {
      spotifyAPIService.getUserProfile(accessToken)
    }

  override suspend fun getTopArtists(
    accessToken: String,
    limit: Int,
    offset: Int,
  ): PagingObject<ArtistObject> = wrapForErrorHandling {
    spotifyAPIService.getTopArtists(accessToken, limit, offset)
  }

  override suspend fun getArtistAlbums(
    accessToken: String,
    artistId: String,
    market: String,
    limit: Int,
    includeGroups: String,
  ): PagingObject<AlbumObject> = wrapForErrorHandling {
    spotifyAPIService.getArtistAlbums(accessToken, artistId, market, limit, includeGroups)
  }

  override suspend fun getAlbumTracks(
    accessToken: String,
    albumId: String,
  ): PagingObject<TrackObject> = wrapForErrorHandling {
    spotifyAPIService.getAlbumTracks(accessToken, albumId)
  }
}
