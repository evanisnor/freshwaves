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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    suspendCoroutine { continuation ->
      handyAuth.authorize(activity) { result ->
        when (result) {
          is HandyAuth.Result.Authorized -> {
            sendSuccessfulAuthorizationBroadcast(activity)
            continuation.resume(SpotifyAuthorization.Response.Success)
          }
          is HandyAuth.Result.Error ->
            continuation.resume(SpotifyAuthorization.Response.Failure)
        }
      }
    }

  override suspend fun getAuthorizationHeader(): String = handyAuth.accessToken().asHeaderValue()

  private fun sendSuccessfulAuthorizationBroadcast(context: Context) {
    LocalBroadcastManager.getInstance(context)
      .sendBroadcast(Intent(authorizationSuccessfulAction))
  }

}