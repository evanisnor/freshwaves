package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.ads.Advertisement
import com.evanisnor.freshwaves.databinding.AdsFreshAlbumCardBinding
import com.evanisnor.freshwaves.databinding.AdsNativeAdViewBinding
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.Instant

@AssistedFactory
interface AdFreshAlbumCardFactory {
  fun create(
    parent: ViewGroup,
    layoutInflater: LayoutInflater = LayoutInflater.from(parent.context),
  ): AdFreshAlbumCard
}

class AdFreshAlbumCard @AssistedInject constructor(
  @Assisted parent: ViewGroup,
  @Assisted private val layoutInflater: LayoutInflater,
) :
  RecyclerView.ViewHolder(
    AdsNativeAdViewBinding.inflate(layoutInflater, parent, false).root,
  ) {

  fun bind(ad: Advertisement) {
    AdsNativeAdViewBinding.bind(itemView).root.apply {
      destroy()
      AdsFreshAlbumCardBinding.inflate(layoutInflater, this, true).apply {
        adIcon.setImageDrawable(ad.icon)
        adChoicesLogo.setImageDrawable(ad.adChoicesLogo)
        adHeadline.text = ad.headline
        adText.text = ad.body
        callToaction.text = ad.callToAction
      }
      ad.nativeAd?.let(this::setNativeAd)

      Firebase.crashlytics.setCustomKey("ad_impression", Instant.now().epochSecond)
    }
  }
}
