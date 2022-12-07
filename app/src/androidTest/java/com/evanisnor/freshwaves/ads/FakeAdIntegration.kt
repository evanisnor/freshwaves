package com.evanisnor.freshwaves.ads

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject
import javax.inject.Singleton

class FakeAdIntegration @Inject constructor() : AdIntegration {
  override fun buildAlbumCardAd(contextualId: String, onLoaded: (Advertisement) -> Unit) {
    // TODO Maybe test with ads?
//    onLoaded(Advertisement(
//      headline = "An ad!",
//      body = "Buy this thing please",
//      callToAction = "NOW",
//      icon = null,
//      adChoicesLogo = null
//    ))
  }
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
