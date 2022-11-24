package com.evanisnor.freshwaves.backend

import com.evanisnor.freshwaves.RootModule
import timber.log.Timber
import java.io.IOException
import java.util.Base64
import javax.inject.Inject

class BackendAPIRepository @Inject constructor(
  @RootModule.ApplicationSignature appSignature: String,
  private val backendAPIService: BackendAPIService,
) {

  private val accessToken: String = Base64.getEncoder().encodeToString("android:$appSignature".toByteArray())
  private val authorization: String = "Basic $accessToken"

  suspend fun reportFavouriteArtists(names: List<String>) {
    try {
      backendAPIService.reportFavoriteArtists(
        authorization = authorization,
        artistList = ArtistList(names),
      )
    } catch (e: IOException) {
      Timber.e(e)
    }
  }
}
