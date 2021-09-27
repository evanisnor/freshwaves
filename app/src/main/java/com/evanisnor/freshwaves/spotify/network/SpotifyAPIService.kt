package com.evanisnor.freshwaves.spotify.network

import com.evanisnor.freshwaves.spotify.network.model.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyAPIService {

    companion object {
        fun create(): SpotifyAPIService = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpotifyAPIService::class.java)
    }

    @GET("v1/me")
    fun getUserProfile(
        @Header("Authorization") accessToken: String
    ): Call<PrivateUserObject>

    @GET("v1/me/top/artists")
    fun getTopArtists(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Call<PagingObject<ArtistObject>>

    @GET("v1/artists/{id}/albums")
    fun getArtistAlbums(
        @Header("Authorization") accessToken: String,
        @Path("id") artistId: String,
        @Query("market") market: String,
        @Query("include_groups") includeGroups: String = "album"
    ): Call<PagingObject<AlbumObject>>

    @GET("v1/albums/{id}/tracks")
    fun getAlbumTracks(
        @Header("Authorization") accessToken: String,
        @Path("id") albumId: String
    ): Call<PagingObject<TrackObject>>
}