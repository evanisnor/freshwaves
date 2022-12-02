package com.evanisnor.freshwaves.spotify.auth

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
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

private class LoginUserFlow(
  private val handyAuthPendingAuthorization: HandyAuth.PendingAuthorization,
) :
  SpotifyAuthorization.PendingAuthorization {
  override suspend fun authorize(context: Context): SpotifyAuthorization.Response =
    when (handyAuthPendingAuthorization.authorize()) {
      is HandyAuth.Result.Authorized -> {
        LocalBroadcastManager.getInstance(context)
          .sendBroadcast(Intent(authorizationSuccessfulAction))
        SpotifyAuthorization.Response.Success
      }
      is HandyAuth.Result.Error -> SpotifyAuthorization.Response.Failure
    }
}

class SpotifyAuthorizationImpl @Inject constructor(
  private val handyAuth: HandyAuth,
) : SpotifyAuthorization {

  override val isAuthorized: Boolean
    get() = handyAuth.isAuthorized

  override suspend fun prepareAuthorization(fragment: Fragment): SpotifyAuthorization.PendingAuthorization =
    LoginUserFlow(handyAuth.prepareAuthorization(fragment))

  override suspend fun logout() {
    handyAuth.logout()
  }

  override suspend fun getAuthorizationHeader(): String = handyAuth.accessToken().asHeaderValue()
}
