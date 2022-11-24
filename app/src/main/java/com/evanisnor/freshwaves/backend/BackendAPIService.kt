package com.evanisnor.freshwaves.backend

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BackendAPIService {

  @POST("/artists")
  suspend fun reportFavoriteArtists(
    @Header("Authorization") authorization: String,
    @Body artistList: ArtistList,
  )
}
