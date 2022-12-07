package com.evanisnor.freshwaves.ads

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject
import javax.inject.Singleton

class FakeAdIntegration @Inject constructor() : AdIntegration {
  override suspend fun buildAlbumCardAd(contextualId: String): Advertisement =
    Advertisement(
      headline = "An ad!",
      body = "Buy this thing please",
      callToAction = "NOW",
      icon = null,
      adChoicesLogo = null,
    )

  override fun clearCache(contextualIdStartsWith: String) {}
}

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [AdIntegrationModule::class],
)
abstract class FakeAdIntegrationModule {
  @Singleton
  @Binds
  abstract fun bindAdIntegration(fakeAdIntegration: FakeAdIntegration): AdIntegration
}
