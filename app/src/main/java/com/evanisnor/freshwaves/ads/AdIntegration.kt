package com.evanisnor.freshwaves.ads

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface AdIntegration {
  fun buildAlbumCardAd(onLoaded: (Advertisement) -> Unit)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AdIntegrationModule {
  @Singleton
  @Binds
  abstract fun bindAdIntegration(adMob: AdMob): AdIntegration
}
