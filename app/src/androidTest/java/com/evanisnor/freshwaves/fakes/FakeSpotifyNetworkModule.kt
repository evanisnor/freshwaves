package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [SpotifyNetworkModule::class]
)
object FakeSpotifyNetworkModule {

  @Provides
  @Singleton
  fun spotifyAPIService(): SpotifyAPIService = FakeSpotifyAPIService(
    behaviorDelegate = MockRetrofit.Builder(
      Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .build()
    )
      .networkBehavior(
        NetworkBehavior.create().apply {
          setFailurePercent(0)
        }
      )
      .build()
      .create(SpotifyAPIService::class.java)
  )

}