package com.evanisnor.freshwaves.spotify.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.evanisnor.freshwaves.authorization.*
import com.evanisnor.freshwaves.system.AppMetadata
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.reflect.KClass


@Singleton
class SpotifyAuthorizationRepository @Inject constructor(
    private val authStateFactory: AuthState.Factory,
    @Named("UserSettings") private val userSettings: DataStore<Preferences>
) {

    companion object {
        private val authStatePreferenceKey = stringPreferencesKey("authState")

        internal val config = AuthServiceConfig(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token"),
        )
    }

    private var _authState: AuthState? = null

    suspend fun authState(): AuthState =
        _authState ?: run {
            _authState = load()
            _authState!!
        }

    suspend fun isAuthorized(): Boolean = authState().isAuthorized

    suspend fun needsTokenRefresh(): Boolean = authState().needsTokenRefresh

    suspend fun accessToken(): String? = authState().accessToken

    suspend fun bearerToken(): String = "Bearer ${authState().accessToken}"

    suspend fun update(request: AuthTokenRequest, response: AuthTokenResponse?) {
        response?.let {
            authState().update(request, it)
            save(authState())
        }
    }

    suspend fun update(error: AuthError?) {
        error?.let {
            authState().update(it)
            save(authState())
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

    fun <A : Activity> activityIntent(context: Context, activityClass: KClass<A>): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, activityClass.java),
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )

// region Persistence

    private suspend fun load(): AuthState {
        val authStateString = userSettings.data.firstOrNull()?.get(authStatePreferenceKey)
        val authState = if (authStateString != null) {
            authStateFactory.create(config, authStateString)
        } else {
            authStateFactory.create(config)
        }
        return authState
    }

    private suspend fun save(authState: AuthState) {
        userSettings.edit { preferences ->
            preferences[authStatePreferenceKey] = authState.toJson()
        }
    }

// endregion
}