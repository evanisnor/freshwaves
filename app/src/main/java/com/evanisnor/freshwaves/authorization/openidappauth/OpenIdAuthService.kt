package com.evanisnor.freshwaves.authorization.openidappauth

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import com.evanisnor.freshwaves.authorization.*
import com.evanisnor.freshwaves.authorization.AuthState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.broadcast
import net.openid.appauth.*

class OpenIdAuthService(
    context: Context,
    private val clientId: String
) : AuthService {

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

    override suspend fun parseAuthResponse(activity: Activity): AuthAuthResponse? {
        AuthorizationResponse.fromIntent(activity.intent)?.let { response ->
            return response.toAuthResponse()
        }
        return null
    }

    override suspend fun parseAuthError(activity: Activity): AuthError? {
        AuthorizationException.fromIntent(activity.intent)?.let { error ->
            return error.toAuthError()
        }

        return null
    }

    override suspend fun performTokenRequest(tokenRequest: AuthTokenRequest) =
        suspendCancellableCoroutine<AuthTokenResponse> { continuation ->
            val request = tokenRequest.toOpenIdTokenRequest()

            authService.performTokenRequest(request) { response, ex ->
                if (response != null) {
                    continuation.resume(response.toAuthTokenResponse(tokenRequest)) {}
                } else if (ex != null) {
                    throw ex.toAuthError()
                }
            }
        }


    override fun createTokenExchangeRequest(authResponse: AuthAuthResponse): AuthTokenRequest =
        authResponse.toOpenIdAuthResponse()
            .createTokenExchangeRequest()
            .toAuthTokenRequest()

    override fun createTokenRefreshRequest(
        config: AuthServiceConfig,
        authState: AuthState
    ): AuthTokenRequest =
        TokenRequest.Builder(config.toOpenIdAuthConfig(), clientId)
            .setScope(null)
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(authState.refreshToken)
            .build()
            .toAuthTokenRequest()

}