package com.evanisnor.freshwaves.spotify.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.evanisnor.freshwaves.authorization.*
import com.evanisnor.freshwaves.system.AppMetadata
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.reflect.KClass


@Singleton
class SpotifyAuthorizationRepository @Inject constructor(
    private val authStateFactory: AuthState.Factory,
    @Named("UserSettings") private val userSettings: SharedPreferences
) {

    companion object {
        private const val userSettingsKey = "authState"

        internal val config = AuthServiceConfig(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token"),
        )
    }

    private var _authState: AuthState? = null
    val authState: AuthState
        get() {
            return _authState ?: run {
                _authState = load()
                _authState!!
            }
        }

    val isAuthorized: Boolean
        get() = authState.isAuthorized

    val needsTokenRefresh: Boolean
        get() = authState.needsTokenRefresh

    val accessToken: String?
        get() = authState.accessToken

    val bearerToken: String
        get() = "Bearer ${authState.accessToken}"

    fun update(request: AuthTokenRequest, response: AuthTokenResponse?) {
        response?.let {
            authState.update(request, it)
            save(authState)
        }
    }

    fun update(error: AuthError?) {
        error?.let {
            authState.update(it)
            save(authState)
        }
    }

    fun authorizationRequest(context: Context) =
        with(AppMetadata()) {
            AuthAuthRequest(
                config = config,
                clientId = spotifyClientId(context),
                responseType = "code",
                redirectUri = Uri.parse(spotifyRedirectUri(context)),
                scope = "user-top-read, user-read-private, user-read-email"
            )
        }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun <A : Activity> activityIntent(context: Context, activityClass: KClass<A>): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, activityClass.java),
            0
        )

    // region Persistence

    private fun load(): AuthState {
        val authStateString = userSettings.getString(userSettingsKey, null)
        val authState = if (authStateString != null) {
            authStateFactory.create(config, authStateString)
        } else {
            authStateFactory.create(config)
        }
        return authState
    }

    private fun save(authState: AuthState) {
        userSettings.edit().putString(userSettingsKey, authState.toJson()).apply()
    }

    // endregion
}