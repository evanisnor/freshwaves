package com.evanisnor.freshwaves.ads

import com.google.android.gms.ads.LoadAdError
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface AdIntegration {

  class LoadFailed(error: LoadAdError) : RuntimeException(
    "Failed to load ad. Cause: ${error.cause ?: "Unknown"}\n${error.responseInfo}",
  )

  suspend fun buildAlbumCardAd(contextualId: String): Advertisement

  fun clearCache(contextualIdStartsWith: String = "")
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AdIntegrationModule {
  @Singleton
  @Binds
  abstract fun bindAdIntegration(adMob: AdMob): AdIntegration
}
