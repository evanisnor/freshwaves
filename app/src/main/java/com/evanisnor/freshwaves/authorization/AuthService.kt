package com.evanisnor.freshwaves.authorization

import android.app.Activity
import android.app.PendingIntent
import android.content.Context

interface AuthService {

    interface Factory {
        fun create(context: Context): AuthService
    }

    fun performAuthorizationRequest(
        request: AuthAuthRequest,
        completedIntent: PendingIntent,
        canceledIntent: PendingIntent
    )

    fun parseAuthResponse(activity: Activity, onAuthResponse: (AuthAuthResponse) -> Unit)

    fun parseAuthError(activity: Activity, onError: (AuthError) -> Unit)

    fun performTokenRequest(
        tokenRequest: AuthTokenRequest,
        onTokenResponse: (AuthTokenResponse) -> Unit,
        onError: (AuthError) -> Unit
    )

    fun createTokenExchangeRequest(authResponse: AuthAuthResponse): AuthTokenRequest

    fun refreshTokens(authState: AuthState)

}