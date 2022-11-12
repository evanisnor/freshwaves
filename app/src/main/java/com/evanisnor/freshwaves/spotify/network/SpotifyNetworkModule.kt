package com.evanisnor.freshwaves.spotify.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import javax.inject.Qualifier
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
          .dispatcher(Dispatcher().apply {
            maxRequestsPerHost = 1
          }).apply {
            interceptors.forEach { addNetworkInterceptor(it) }
          }
          .build()
      )
      .build()
      .create(SpotifyAPIService::class.java)

  @Provides
  @ElementsIntoSet
  fun interceptors(
    @DebugInterceptors debugInterceptors: Set<@JvmSuppressWildcards Interceptor>,
  ): Set<Interceptor> =
    setOf(RateLimitInterceptor(delayMs = 500), *debugInterceptors.toTypedArray())


  // region Debug Interceptor support

  @Qualifier
  @Retention(AnnotationRetention.RUNTIME)
  annotation class DebugInterceptors

  @Provides
  @DebugInterceptors
  @ElementsIntoSet
  fun debugInterceptors(): Set<Interceptor> = emptySet()

  // endregion
}