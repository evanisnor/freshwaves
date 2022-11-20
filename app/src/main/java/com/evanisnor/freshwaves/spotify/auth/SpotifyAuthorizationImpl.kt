package com.evanisnor.freshwaves.spotify.auth

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization.Companion.authorizationSuccessfulAction
import com.evanisnor.handyauth.client.HandyAuth
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpotifyAuthorizationModule {
  @Singleton
  @Binds
  abstract fun bindSpotifyAuthorization(impl: SpotifyAuthorizationImpl): SpotifyAuthorization
}

class SpotifyAuthorizationImpl @Inject constructor(
  private val handyAuth: HandyAuth,
) : SpotifyAuthorization {

  override val isAuthorized: Boolean
    get() = handyAuth.isAuthorized

  override suspend fun authorize(activity: ComponentActivity): SpotifyAuthorization.Response =
    when (handyAuth.authorize(activity)) {
      is HandyAuth.Result.Authorized -> {
        sendSuccessfulAuthorizationBroadcast(activity)
        SpotifyAuthorization.Response.Success
      }
      is HandyAuth.Result.Error -> SpotifyAuthorization.Response.Failure
    }

  override suspend fun getAuthorizationHeader(): String = handyAuth.accessToken().asHeaderValue()

  private fun sendSuccessfulAuthorizationBroadcast(context: Context) {
    LocalBroadcastManager.getInstance(context)
      .sendBroadcast(Intent(authorizationSuccessfulAction))
  }
}
