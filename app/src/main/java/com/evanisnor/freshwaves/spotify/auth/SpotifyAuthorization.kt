package com.evanisnor.freshwaves.spotify.auth

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.evanisnor.handyauth.client.HandyAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyAuthorization @Inject constructor(
    private val handyAuth: HandyAuth
) {

    companion object {
        const val authorizationSuccessfulAction = "authorization-successful"
    }

    fun checkLogin(loggedIn: () -> Unit, notLoggedIn: () -> Unit) {
        if (handyAuth.isAuthorized) {
            loggedIn()
        } else {
            notLoggedIn()
        }
    }

    suspend fun getBearerToken(): String = handyAuth.accessToken().asHeaderValue()

    fun performLoginAuthorization(
        activity: ComponentActivity,
        onSuccess: () -> Unit,
        onCancel: () -> Unit
    ) {
        handyAuth.authorize(activity) { result ->
            when (result) {
                is HandyAuth.Result.Success -> {
                    sendSuccessfulAuthorizationBroadcast(activity)
                    onSuccess()
                }
                is HandyAuth.Result.Error -> onCancel()
            }
        }
    }

    private fun sendSuccessfulAuthorizationBroadcast(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(authorizationSuccessfulAction))
    }

}