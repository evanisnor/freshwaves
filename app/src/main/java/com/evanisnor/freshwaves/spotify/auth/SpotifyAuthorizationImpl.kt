package com.evanisnor.freshwaves.spotify.auth

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
  private val onResponse: suspend (SpotifyAuthorization.Response) -> Unit,
) :
  SpotifyAuthorization.PendingAuthorization {
  override suspend fun authorize(): SpotifyAuthorization.Response =
    when (handyAuthPendingAuthorization.authorize()) {
      is HandyAuth.Result.Authorized -> {
        SpotifyAuthorization.Response.Success
      }
      is HandyAuth.Result.Error -> SpotifyAuthorization.Response.Failure
    }.also {
      onResponse(it)
    }
}

class SpotifyAuthorizationImpl @Inject constructor(
  private val handyAuth: HandyAuth,
) : SpotifyAuthorization {

  private val _latestResponse =
    MutableStateFlow(
      if (isAuthorized) {
        SpotifyAuthorization.Response.Success
      } else {
        SpotifyAuthorization.Response.Failure
      },
    )

  override val isAuthorized: Boolean
    get() = handyAuth.isAuthorized

  override val latestResponse: Flow<SpotifyAuthorization.Response> = _latestResponse.asStateFlow()

  override suspend fun prepareAuthorization(fragment: Fragment): SpotifyAuthorization.PendingAuthorization =
    LoginUserFlow(handyAuth.prepareAuthorization(fragment)) {
      // TODO Remove this side-effect
      if (it is SpotifyAuthorization.Response.Success) {
        LocalBroadcastManager.getInstance(fragment.requireContext())
          .sendBroadcast(Intent(authorizationSuccessfulAction))
      }
      _latestResponse.emit(it)
    }

  override suspend fun logout() {
    handyAuth.logout()
    _latestResponse.emit(SpotifyAuthorization.Response.Failure)
  }

  override suspend fun getAuthorizationHeader(): String = handyAuth.accessToken().asHeaderValue()
}
