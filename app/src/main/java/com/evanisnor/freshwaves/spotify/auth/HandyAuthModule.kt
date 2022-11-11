package com.evanisnor.freshwaves.spotify.auth

import android.app.Application
import com.evanisnor.freshwaves.system.AppMetadata
import com.evanisnor.handyauth.client.HandyAuth
import com.evanisnor.handyauth.client.HandyAuthConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi

@Module
@InstallIn(SingletonComponent::class)
object HandyAuthModule {

  @Provides
  fun handyAuthConfig(application: Application, appMetadata: AppMetadata): HandyAuthConfig =
    HandyAuthConfig(
      clientId = appMetadata.spotifyClientId(application),
      redirectUrl = appMetadata.spotifyRedirectUri(application),
      authorizationUrl = "https://accounts.spotify.com/authorize",
      tokenUrl = "https://accounts.spotify.com/api/token",
      scopes = listOf("user-top-read", "user-read-private", "user-read-email")
    )

  @OptIn(DelicateCoroutinesApi::class)
  @Provides
  fun handyAuth(application: Application, handyAuthConfig: HandyAuthConfig): HandyAuth =
    HandyAuth.create(
      application = application,
      config = handyAuthConfig
    )

}