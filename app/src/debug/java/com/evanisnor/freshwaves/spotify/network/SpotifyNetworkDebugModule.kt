package com.evanisnor.freshwaves.spotify.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object SpotifyNetworkDebugModule {

  @Provides
  @ElementsIntoSet
  fun interceptors(): Set<Interceptor> = setOf(
    HttpLoggingInterceptor().apply {
      setLevel(HttpLoggingInterceptor.Level.BASIC)
    },
  )
}
