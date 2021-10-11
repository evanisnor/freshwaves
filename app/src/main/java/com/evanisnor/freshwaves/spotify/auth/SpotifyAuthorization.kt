package com.evanisnor.freshwaves.spotify.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.evanisnor.freshwaves.authorization.AuthError
import com.evanisnor.freshwaves.authorization.AuthService
import com.evanisnor.freshwaves.authorization.AuthTokenRequest
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
        } else if (repository.needsTokenRefresh) {
            refreshTokens()
            loggedIn()
        } else {
            loggedIn()
        }
    }

    fun useBearerToken(withBearerToken: (String) -> Unit) {
        if (repository.needsTokenRefresh) {
            refreshTokens()
        }
        withBearerToken(repository.bearerToken)
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


    fun confirmAuthorization(
        activity: Activity,
        onAuthorizationError: (AuthError) -> Unit
    ) {
        with(authServiceFactory.create(context)) {
            parseAuthError(activity) { error ->
                Log.e("SpotifyAuthorization", "Failed to authorize: $error")
                onAuthorizationError(error)
            }

            parseAuthResponse(activity) { response ->
                exchangeAuthorizationCode(
                    tokenExchangeRequest = createTokenExchangeRequest(response),
                    onAuthorized = { sendSuccessfulAuthorizationBroadcast(context) },
                    onAuthorizationError = onAuthorizationError
                )
            }
        }
    }

    private fun sendSuccessfulAuthorizationBroadcast(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(authorizationSuccessfulAction))
    }

    private fun exchangeAuthorizationCode(
        tokenExchangeRequest: AuthTokenRequest,
        onAuthorized: () -> Unit,
        onAuthorizationError: (AuthError) -> Unit
    ) {
        with(authServiceFactory.create(context)) {
            performTokenRequest(
                tokenRequest = tokenExchangeRequest,
                onTokenResponse = { response ->
                    repository.update(tokenExchangeRequest, response)
                    onAuthorized()
                },
                onError = { error ->
                    repository.update(error)
                    onAuthorizationError(error)
                }
            )
        }
    }

    private fun refreshTokens() {
        with(authServiceFactory.create(context)) {
            refreshTokens(repository.authState)
        }
    }

}