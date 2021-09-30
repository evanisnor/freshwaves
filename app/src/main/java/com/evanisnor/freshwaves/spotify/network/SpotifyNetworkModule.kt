package com.evanisnor.freshwaves.spotify.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object SpotifyNetworkModule {

    @Provides
    fun spotifyAPIService() = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .dispatcher(Dispatcher().apply {
                    maxRequestsPerHost = 1
                })
                .addNetworkInterceptor {
                    Thread.sleep(100)
                    it.proceed(it.request())
                }
                .build()
        )
        .build()
        .create(SpotifyAPIService::class.java)

}