package com.evanisnor.freshwaves.backend

import com.evanisnor.freshwaves.ext.wrapHttpException
import com.evanisnor.freshwaves.system.SystemModule
import timber.log.Timber
import java.util.Base64
import javax.inject.Inject

class BackendAPIRepository @Inject constructor(
  @SystemModule.ApplicationSignature appSignature: String,
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
    } catch (e: Throwable) {
      Timber.e(e.wrapHttpException())
    }
  }
}
