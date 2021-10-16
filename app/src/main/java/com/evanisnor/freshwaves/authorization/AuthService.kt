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

    suspend fun parseAuthResponse(activity: Activity): AuthAuthResponse?

    suspend fun parseAuthError(activity: Activity): AuthError?

    suspend fun performTokenRequest(tokenRequest: AuthTokenRequest): AuthTokenResponse

    fun createTokenExchangeRequest(authResponse: AuthAuthResponse): AuthTokenRequest

    fun createTokenRefreshRequest(config: AuthServiceConfig, authState: AuthState): AuthTokenRequest
}