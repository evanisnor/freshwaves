package com.evanisnor.freshwaves.spotify.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object SpotifyNetworkModule {

  @Provides
  fun spotifyAPIService(interceptors: Set<@JvmSuppressWildcards Interceptor>): SpotifyAPIService =
    Retrofit.Builder()
      .baseUrl("https://api.spotify.com/")
      .addConverterFactory(MoshiConverterFactory.create())
      .client(
        OkHttpClient.Builder()
          .dispatcher(
            Dispatcher().apply {
              maxRequestsPerHost = 1
            },
          )
          .addNetworkInterceptor(RateLimitInterceptor(delayMs = 500))
          .apply {
            interceptors.forEach { addNetworkInterceptor(it) }
          }
          .build(),
      )
      .build()
      .create(SpotifyAPIService::class.java)
      .let { SpotifyAPIServiceErrorWrappingDelegate(it) }

  @Provides
  @ElementsIntoSet
  fun interceptors(): Set<Interceptor> = emptySet()
}
