package com.evanisnor.freshwaves.ads

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject
import javax.inject.Singleton

class FakeAlbumCardAdRepository @Inject constructor() : AlbumCardAdRepository {

  override suspend fun generateAlbumCardAds(numberOfAds: Int): List<Advertisement> = emptyList()

  override fun destroyAds() {}
}

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [AlbumCardAdRepositoryModule::class],
)
abstract class FakeAlbumCardAdRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindAlbumCardAdRepository(
    fakeAlbumCardAdRepository: FakeAlbumCardAdRepository,
  ): AlbumCardAdRepository
}
