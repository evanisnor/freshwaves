package com.evanisnor.freshwaves.spotify

import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.model.AlbumObject
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PagingObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.spotify.network.model.TrackObject

class FakeSpotifyAPIService : SpotifyAPIService {

  var privateUser: PrivateUserObject? = null
  var topArtists: List<ArtistObject>? = null
  var artistAlbums: List<AlbumObject>? = null
  var albumTracks: List<TrackObject>? = null

  override suspend fun getUserProfile(accessToken: String): PrivateUserObject =
    requireNotNull(privateUser)

  override suspend fun getTopArtists(
      accessToken: String,
      limit: Int,
      offset: Int,
  ): PagingObject<ArtistObject> =
    PagingObject(requireNotNull(topArtists).slice(offset until offset + limit))

  override suspend fun getArtistAlbums(
      accessToken: String,
      artistId: String,
      market: String,
      limit: Int,
      includeGroups: String,
  ): PagingObject<AlbumObject> = PagingObject(requireNotNull(artistAlbums))

  override suspend fun getAlbumTracks(
      accessToken: String,
      albumId: String,
  ): PagingObject<TrackObject> = PagingObject(requireNotNull(albumTracks))
}