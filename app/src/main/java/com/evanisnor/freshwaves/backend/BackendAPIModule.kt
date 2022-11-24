package com.evanisnor.freshwaves.backend

import com.evanisnor.freshwaves.spotify.network.RateLimitInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object BackendAPIModule {

  @Qualifier
  @Retention(AnnotationRetention.RUNTIME)
  annotation class BackendUrl

  // @Backend provider located in /debug and /release source sets

  @Provides
  fun backendAPIService(
    @BackendUrl backendUrl: String,
    interceptors: Set<@JvmSuppressWildcards Interceptor>,
  ): BackendAPIService =
    Retrofit.Builder()
      .baseUrl(backendUrl)
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
      ).build()
      .create(BackendAPIService::class.java)
}
