package com.evanisnor.freshwaves.ads

import android.graphics.drawable.Drawable
import com.google.android.gms.ads.nativead.NativeAd

/**
 * Nice wrapper class for [NativeAd]
 */
class Advertisement(
  val headline: String?,
  val body: String?,
  val callToAction: String?,
  val icon: Drawable?,
  val adChoicesLogo: Drawable?,
  val nativeAd: NativeAd? = null,
) {

  constructor(nativeAd: NativeAd?) : this(
    nativeAd?.headline,
    nativeAd?.body,
    nativeAd?.callToAction,
    nativeAd?.icon?.drawable,
    nativeAd?.adChoicesInfo?.images?.first()?.drawable,
    nativeAd,
  )
}
