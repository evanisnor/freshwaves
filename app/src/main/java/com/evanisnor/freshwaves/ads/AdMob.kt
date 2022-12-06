package com.evanisnor.freshwaves.ads

import android.annotation.SuppressLint
import android.content.Context
import com.evanisnor.freshwaves.system.AppMetadata
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdMob @Inject constructor(
  @ApplicationContext private val context: Context,
  appMetadata: AppMetadata,
) : AdIntegration {

  private val albumCardId = appMetadata.adMobAdAlbumCard(context)

  init {
    MobileAds.initialize(context) {
      Timber.d("AdMob Initialized")
    }
  }

  @SuppressLint("VisibleForTests")
  override fun buildAlbumCardAd(onLoaded: (Advertisement) -> Unit) {
    AdLoader.Builder(context, albumCardId)
      .forNativeAd {
        onLoaded(Advertisement(it))
      }
      .withAdListener(
        object : AdListener() {
          override fun onAdFailedToLoad(error: LoadAdError) {
            Timber.e("Failed to load Album Card Ad. Cause: ${error.cause ?: "Unknown"}\n${error.responseInfo}")
          }
        },
      )
      .build()
      .loadAd(AdRequest.Builder().build())
  }
}
