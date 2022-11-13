package com.evanisnor.freshwaves.spotify.network

import com.evanisnor.freshwaves.spotify.network.model.AlbumObject
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PagingObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.spotify.network.model.TrackObject
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyAPIService {

  @GET("v1/me")
  suspend fun getUserProfile(
    @Header("Authorization") accessToken: String,
  ): PrivateUserObject

  @GET("v1/me/top/artists")
  suspend fun getTopArtists(
    @Header("Authorization") accessToken: String,
    @Query("limit") limit: Int = 50,
    @Query("offset") offset: Int = 0,
  ): PagingObject<ArtistObject>

  @GET("v1/artists/{id}/albums")
  suspend fun getArtistAlbums(
    @Header("Authorization") accessToken: String,
    @Path("id") artistId: String,
    @Query("market") market: String,
    @Query("limit") limit: Int = 5,
    @Query("include_groups") includeGroups: String = "album",
  ): PagingObject<AlbumObject>

  @GET("v1/albums/{id}/tracks")
  suspend fun getAlbumTracks(
    @Header("Authorization") accessToken: String,
    @Path("id") albumId: String,
  ): PagingObject<TrackObject>
}
