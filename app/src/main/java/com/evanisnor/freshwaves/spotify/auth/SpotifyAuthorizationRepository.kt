package com.evanisnor.freshwaves.spotify.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.evanisnor.freshwaves.system.AppMetadata
import net.openid.appauth.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.reflect.KClass


@Singleton
class SpotifyAuthorizationRepository @Inject constructor(
    @Named("UserSettings") private val userSettings: SharedPreferences
) {

    companion object {
        private const val userSettingsKey = "authState"

        private val config = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token"),
        )
    }

    private var _authState: AuthState? = null
    private val authState: AuthState
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

    fun update(response: TokenResponse?, error: AuthorizationException?) {
        authState.update(response, error)
        save(authState)
    }

    fun authorizationRequest(context: Context) =
        with(AppMetadata()) {
            AuthorizationRequest.Builder(
                config,
                spotifyClientId(context),
                ResponseTypeValues.CODE,
                Uri.parse(spotifyRedirectUri(context))
            ).apply {
                setScope("user-top-read, user-read-private, user-read-email")
            }.build()
        }

    fun refreshRequest() = authState.createTokenRefreshRequest()

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
            AuthState.jsonDeserialize(authStateString)
        } else {
            AuthState(config)
        }
        return authState
    }

    private fun save(authState: AuthState) {
        userSettings.edit().putString(userSettingsKey, authState.jsonSerializeString()).apply()
    }

    // endregion
}