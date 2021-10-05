package com.evanisnor.freshwaves.spotify.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class SpotifyAuthorization @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SpotifyAuthorizationRepository
) {

    fun checkLogin(loggedIn: () -> Unit, notLoggedIn: () -> Unit) {
        if (!repository.isAuthorized) {
            notLoggedIn()
        } else {
            loggedIn()
        }
    }

    fun useBearerToken(withBearerToken: (String) -> Unit) {
        if (repository.needsTokenRefresh) {
            refreshAccessToken(
                withBearerToken = withBearerToken,
                withError = {
                    Log.e("SpotifyRepository", "Authorization error: ${it.toJsonString()}")
                })
        } else {
            withBearerToken(repository.bearerToken)
        }
    }

    fun <SuccessActivity : Activity, CancelActivity : Activity> performLoginAuthorization(
        activity: Activity,
        activityOnSuccess: KClass<SuccessActivity>,
        activityOnCancel: KClass<CancelActivity>
    ) {
        with(AuthorizationService(activity)) {
            performAuthorizationRequest(
                repository.authorizationRequest(activity),
                repository.activityIntent(activity, activityOnSuccess),
                repository.activityIntent(activity, activityOnCancel)
            )
        }
    }


    fun confirmAuthorization(
        activity: Activity,
        onAuthorized: () -> Unit,
        onAuthorizationError: (AuthorizationException) -> Unit
    ) {
        AuthorizationResponse.fromIntent(activity.intent)
            ?.createTokenExchangeRequest()
            ?.let { tokenRequest ->
                exchangeAuthorizationCode(
                    activity = activity,
                    tokenRequest = tokenRequest,
                    onAuthorized = onAuthorized,
                    onAuthorizationError = onAuthorizationError
                )
            }

        AuthorizationException.fromIntent(activity.intent)?.let { error ->
            Log.e("SpotifyAuthorization", "Failed to authorize: ${error.toJsonString()}")
            onAuthorizationError(error)
        }
    }

    private fun exchangeAuthorizationCode(
        activity: Activity,
        tokenRequest: TokenRequest,
        onAuthorized: () -> Unit,
        onAuthorizationError: (AuthorizationException) -> Unit
    ) {
        with(AuthorizationService(activity)) {
            performTokenRequest(tokenRequest) { response, error ->
                repository.update(response, error)

                response?.let {
                    onAuthorized()
                }

                error?.let {
                    onAuthorizationError(error)
                }
            }
        }
    }

    private fun refreshAccessToken(
        withBearerToken: (String) -> Unit,
        withError: (AuthorizationException) -> Unit
    ) {
        with(AuthorizationService(context)) {
            performTokenRequest(repository.refreshRequest()) { response, error ->
                repository.update(response, error)

                response?.let {
                    withBearerToken(repository.bearerToken)
                }

                error?.let {
                    withError(error)
                }
            }
        }
    }

}