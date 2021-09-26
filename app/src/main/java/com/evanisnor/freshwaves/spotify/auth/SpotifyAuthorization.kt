package com.evanisnor.freshwaves.spotify.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.evanisnor.freshwaves.LoginActivity
import com.evanisnor.freshwaves.MainActivity
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

    fun authorizeIfNeeded(activity: Activity, allGood: () -> Unit) {
        if (!authState.isAuthorized || authState.lastAuthorizationResponse == null) {
            with(AppMetadata()) {
                buildAuthorizationRequest(
                    clientId = spotifyClientId(activity),
                    redirectUri = spotifyRedirectUri(activity)
                ).let { authorizationRequest ->
                    authorize(
                        activity = activity,
                        authorizationRequest = authorizationRequest,
                        activityOnSuccess = MainActivity::class,
                        activityOnCancel = LoginActivity::class,
                    )
                }
            }
        } else if (authState.isAuthorized && authState.needsTokenRefresh) {
            with(AuthorizationService(activity)) {
                performTokenRequest(authState.createTokenRefreshRequest()) { response, error ->
                    authState.update(response, error)
                    allGood()
                }
            }
        } else {
            allGood()
        }
    }

    fun authorizedAction(
        context: Context,
        withAccessToken: (String) -> Unit,
        withError: (AuthorizationException) -> Unit
    ) {
        with(AuthorizationService(context)) {
            if (authState.needsTokenRefresh) {
                authState.performActionWithFreshTokens(this) { accessToken, _, e ->
                    accessToken?.let {
                        withAccessToken("Bearer $it")
                    }
                    e?.let {
                        withError(e)
                    }
                }
            } else {
                withAccessToken("Bearer ${authState.accessToken}")
            }
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

    @SuppressLint("UnspecifiedImmutableFlag")
    fun <SuccessActivity : Activity, CancelActivity : Activity> authorize(
        activity: Activity,
        authorizationRequest: AuthorizationRequest,
        activityOnSuccess: KClass<SuccessActivity>,
        activityOnCancel: KClass<CancelActivity>
    ) {
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