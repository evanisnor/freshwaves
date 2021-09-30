package com.evanisnor.freshwaves.spotify.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.evanisnor.freshwaves.system.AppMetadata
import net.openid.appauth.*
import kotlin.reflect.KClass

class SpotifyAuthorization(
    userSettings: SharedPreferences
) {

    companion object {
        private val config = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token"),
        )
    }

    private val authState: AuthState

    init {
        val authStateString = userSettings.getString("authState", null)
        authState = if (authStateString == null) {
            AuthState(config)
        } else {
            AuthState.jsonDeserialize(authStateString)
        }
    }

    fun provideAccessToken(context: Context, withAccessToken: (String) -> Unit) {
        refreshToken(context,
            withAccessToken = withAccessToken,
            withError = {
                Log.e("SpotifyRepository", "Authorization error: ${it.toJsonString()}")
            })
    }

    fun checkLogin(loggedIn: () -> Unit, notLoggedIn: () -> Unit) {
        if (!authState.isAuthorized) {
            notLoggedIn()
        } else {
            loggedIn()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun <SuccessActivity : Activity, CancelActivity : Activity> performLoginAuthorization(
        activity: Activity,
        activityOnSuccess: KClass<SuccessActivity>,
        activityOnCancel: KClass<CancelActivity>
    ) {
        with(AppMetadata()) {
            buildAuthorizationRequest(
                clientId = spotifyClientId(activity),
                redirectUri = spotifyRedirectUri(activity)
            ).let { authorizationRequest ->
                with(AuthorizationService(activity)) {
                    performAuthorizationRequest(
                        authorizationRequest,
                        PendingIntent.getActivity(
                            activity,
                            0,
                            Intent(activity, activityOnSuccess.java),
                            0
                        ),
                        PendingIntent.getActivity(
                            activity,
                            0,
                            Intent(activity, activityOnCancel.java),
                            0
                        )
                    )
                }
            }
        }
    }

    fun authorize(
        activity: Activity,
        onAuthorized: () -> Unit,
        onAuthorizationError: (AuthorizationException) -> Unit
    ) {
        val response = AuthorizationResponse.fromIntent(activity.intent)
        val error = AuthorizationException.fromIntent(activity.intent)

        when {
            error != null -> {
                Log.e("SpotifyAuthorization", "Failed to authorize: ${error.toJsonString()}")
                onAuthorizationError(error)
            }
            response != null -> {
                with(AuthorizationService(activity)) {
                    performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, e ->
                        authState.update(tokenResponse, e)
                        onAuthorized()
                    }
                }
            }
            authState.needsTokenRefresh -> refreshToken(
                context = activity,
                withAccessToken = { onAuthorized() },
                withError = onAuthorizationError
            )
            else -> {
                onAuthorized()
            }
        }
    }

    fun refreshToken(
        context: Context,
        withAccessToken: (String) -> Unit,
        withError: (AuthorizationException) -> Unit
    ) {
        if (authState.needsTokenRefresh) {
            with(AuthorizationService(context)) {
                performTokenRequest(authState.createTokenRefreshRequest()) { response, error ->
                    authState.update(response, error)

                    error?.let {
                        withError(error)
                        return@performTokenRequest
                    }

                    withAccessToken("Bearer ${authState.accessToken}")
                }
            }
        } else {
            withAccessToken("Bearer ${authState.accessToken}")
        }
    }

    private fun buildAuthorizationRequest(clientId: String, redirectUri: String) =
        AuthorizationRequest.Builder(
            config,
            clientId,
            ResponseTypeValues.CODE,
            Uri.parse(redirectUri)
        ).apply {
            setScope("user-top-read, user-read-private, user-read-email")
        }.build()

}