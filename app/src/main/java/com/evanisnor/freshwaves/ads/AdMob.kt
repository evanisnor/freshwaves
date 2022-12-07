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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdMob @Inject constructor(
  @ApplicationContext private val context: Context,
  appMetadata: AppMetadata,
) : AdIntegration {

  private val mainScope = CoroutineScope(Dispatchers.Main.immediate)
  private val albumCardId = appMetadata.adMobAdAlbumCard(context)
  private val cache = mutableMapOf<String, Advertisement>()

  init {
    MobileAds.initialize(context) {
      Timber.d("AdMob Initialized")
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @SuppressLint("VisibleForTests")
  override suspend fun buildAlbumCardAd(contextualId: String): Advertisement =
    suspendCancellableCoroutine { continuation ->
      cache[contextualId]?.let {
        continuation.resume(it) {}
        return@suspendCancellableCoroutine
      }

      mainScope.launch {
        Timber.i("Requesting native advertisement for $contextualId")
        AdLoader.Builder(context, albumCardId)
          .forNativeAd {
            val ad = Advertisement(it)
            cache[contextualId] = ad
            continuation.resume(ad) {}
          }
          .withAdListener(
            object : AdListener() {
              override fun onAdFailedToLoad(error: LoadAdError) {
                continuation.cancel(AdIntegration.LoadFailed(error))
              }
            },
          )
          .build()
          .loadAd(AdRequest.Builder().build())
      }
    }

  override fun clearCache(contextualIdStartsWith: String) {
    cache
      .filter { it.key.startsWith(contextualIdStartsWith) }
      .forEach {
        it.value.nativeAd?.destroy()
        cache.remove(it.key)
      }
  }
}
