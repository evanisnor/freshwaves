package com.evanisnor.freshwaves.ads

import com.evanisnor.freshwaves.FeatureFlags
import com.google.android.gms.ads.LoadAdError
import dagger.Module
import dagger.Provides
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

class DisabledAdIntegration : AdIntegration {
  override suspend fun buildAlbumCardAd(contextualId: String): Advertisement {
    throw IllegalStateException("Ad integration is disabled.")
  }

  override fun clearCache(contextualIdStartsWith: String) = Unit
}

@Module
@InstallIn(SingletonComponent::class)
class AdIntegrationModule {
  @Singleton
  @Provides
  fun bindAdIntegration(adMob: AdMob): AdIntegration {
    return if (FeatureFlags.ENABLE_ADVERTISEMENTS) {
      adMob
    } else {
      DisabledAdIntegration()
    }
  }
}
