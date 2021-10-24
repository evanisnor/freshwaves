package com.evanisnor.freshwaves.spotify.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.evanisnor.freshwaves.authorization.AuthError
import com.evanisnor.freshwaves.authorization.AuthService
import com.evanisnor.freshwaves.authorization.AuthTokenRequest
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class SpotifyAuthorization @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authServiceFactory: AuthService.Factory,
    private val repository: SpotifyAuthorizationRepository
) {

    companion object {
        const val authorizationSuccessfulAction = "authorization-successful"
    }

    fun checkLogin(loggedIn: () -> Unit, notLoggedIn: () -> Unit) {
        if (!repository.isAuthorized) {
            notLoggedIn()
        } else {
            loggedIn()
        }
    }

    suspend fun getBearerToken(): String =
        if (repository.needsTokenRefresh) {
            refreshToken()
        } else {
            repository.bearerToken
        }


    fun <SuccessActivity : Activity, CancelActivity : Activity> performLoginAuthorization(
        activity: Activity,
        activityOnSuccess: KClass<SuccessActivity>,
        activityOnCancel: KClass<CancelActivity>
    ) {
        with(authServiceFactory.create(activity)) {
            performAuthorizationRequest(
                repository.authorizationRequest(activity),
                repository.activityIntent(activity, activityOnSuccess),
                repository.activityIntent(activity, activityOnCancel)
            )
        }
    }


    suspend fun confirmAuthorization(activity: Activity) {
        if (repository.isAuthorized) {
            return
        }

        with(authServiceFactory.create(context)) {
            parseAuthError(activity)?.let { error ->
                Log.e("SpotifyAuthorization", "Failed to authorize: $error")
                Firebase.crashlytics.recordException(error)
            }

            parseAuthResponse(activity)?.let { response ->
                val exchangeRequest = createTokenExchangeRequest(response)
                exchangeAuthorizationCode(exchangeRequest)
                sendSuccessfulAuthorizationBroadcast(context)
            }
        }
    }

    private fun sendSuccessfulAuthorizationBroadcast(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(authorizationSuccessfulAction))
    }

    private suspend fun exchangeAuthorizationCode(tokenExchangeRequest: AuthTokenRequest) {
        with(authServiceFactory.create(context)) {
            try {
                val response = performTokenRequest(tokenExchangeRequest)
                repository.update(tokenExchangeRequest, response)
            } catch (authError: AuthError) {
                repository.update(authError)
                Firebase.crashlytics.recordException(authError)
            }
        }
    }

    private suspend fun refreshToken(): String {
        with(authServiceFactory.create(context)) {
            val tokenRefreshRequest = createTokenRefreshRequest(
                SpotifyAuthorizationRepository.config,
                repository.authState
            )

            try {
                val response = performTokenRequest(tokenRefreshRequest)
                repository.update(tokenRefreshRequest, response)
                return repository.bearerToken
            } catch (authError: AuthError) {
                repository.update(authError)
                Firebase.crashlytics.recordException(authError)
            }
        }

        return repository.bearerToken
    }

}