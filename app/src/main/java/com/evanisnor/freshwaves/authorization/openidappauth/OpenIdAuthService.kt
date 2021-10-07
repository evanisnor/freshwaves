package com.evanisnor.freshwaves.authorization.openidappauth

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import com.evanisnor.freshwaves.authorization.*
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

class OpenIdAuthService(context: Context) : AuthService {

    private val authService = AuthorizationService(context)

    override fun performAuthorizationRequest(
        request: AuthAuthRequest,
        completedIntent: PendingIntent,
        canceledIntent: PendingIntent
    ) {
        authService.performAuthorizationRequest(
            request.toOpenIdAuthRequest(),
            completedIntent,
            canceledIntent
        )
    }

    override fun parseAuthResponse(
        activity: Activity,
        onAuthResponse: (AuthAuthResponse) -> Unit
    ) {
        AuthorizationResponse.fromIntent(activity.intent)?.let { response ->
            onAuthResponse(response.toAuthResponse())
        }
    }

    override fun parseAuthError(activity: Activity, onError: (AuthError) -> Unit) {
        AuthorizationException.fromIntent(activity.intent)?.let { error ->
            onError(error.toAuthError())
        }
    }

    override fun performTokenRequest(
        tokenRequest: AuthTokenRequest,
        onTokenResponse: (AuthTokenResponse) -> Unit,
        onError: (AuthError) -> Unit
    ) {
        val request = tokenRequest.toOpenIdTokenRequest()

        authService.performTokenRequest(request) { response, ex ->
            if (response != null) {
                onTokenResponse(response.toAuthTokenResponse(tokenRequest))
            } else if (ex != null) {
                onError(ex.toAuthError())
            }
        }
    }

    override fun createTokenExchangeRequest(authResponse: AuthAuthResponse): AuthTokenRequest =
        authResponse.toOpenIdAuthResponse()
            .createTokenExchangeRequest()
            .toAuthTokenRequest()

    override fun createTokenRefreshRequest(authState: AuthState): AuthTokenRequest {
        if (authState !is OpenIdAuthState) {
            throw UnsupportedOperationException("Unable to create Token Refresh Request using another auth provider.")
        }

        val refreshRequest = authState.authState.createTokenRefreshRequest()
        return refreshRequest.toAuthTokenRequest()
    }

}