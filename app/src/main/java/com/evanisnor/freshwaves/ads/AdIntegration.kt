package com.evanisnor.freshwaves.ads

import com.google.android.gms.ads.nativead.NativeAd

interface AdIntegration {

  fun buildAlbumCardAd(onLoaded: (NativeAd) -> Unit)
}
