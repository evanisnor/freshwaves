package com.evanisnor.freshwaves.ads

import com.evanisnor.freshwaves.FeatureFlags
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface AlbumCardAdRepository {
  suspend fun generateAlbumCardAds(numberOfAds: Int): List<Advertisement>

  fun destroyAds()
}

class AlbumCardAdRepositoryImpl
  @Inject
  constructor(
    private val adIntegration: AdIntegration,
  ) : AlbumCardAdRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable -> Timber.e(throwable) }

    override suspend fun generateAlbumCardAds(numberOfAds: Int): List<Advertisement> =
      if (FeatureFlags.ENABLE_ADVERTISEMENTS) {
        withContext(scope.coroutineContext) {
          val ads = mutableListOf<CompletableDeferred<Advertisement>>()
          for (n in 0 until numberOfAds) {
            ads.add(CompletableDeferred())
            scope.launch(exceptionHandler) {
              val ad = adIntegration.buildAlbumCardAd("adCard-$n")
              ads[n].complete(ad)
            }
          }
          ads.awaitAll()
        }
      } else {
        emptyList()
      }

    override fun destroyAds() {
      adIntegration.clearCache("adCard")
    }
  }

@Module
@InstallIn(SingletonComponent::class)
abstract class AlbumCardAdRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindAlbumCardAdRepository(albumCardAdRepositoryImpl: AlbumCardAdRepositoryImpl): AlbumCardAdRepository
}
