package com.evanisnor.freshwaves.fakes

import android.app.Activity
import android.app.PendingIntent
import android.net.Uri
import com.evanisnor.freshwaves.authorization.*

class FakeAuthService : AuthService {

    private val fakeConfig = AuthServiceConfig(
        Uri.parse("https://fake.url/authorize"),
        Uri.parse("https://fake.url/token")
    )

    override fun performAuthorizationRequest(
        request: AuthAuthRequest,
        completedIntent: PendingIntent,
        canceledIntent: PendingIntent
    ) {
    }

    override suspend fun parseAuthResponse(activity: Activity): AuthAuthResponse =
        AuthAuthResponse(
            request = AuthAuthRequest(
                config = fakeConfig,
                clientId = "000000000000000000000",
                responseType = "fake",
                redirectUri = Uri.parse("https://fake.url/redirect"),
                scope = "fake"
            )
        )

    override suspend fun parseAuthError(activity: Activity): AuthError =
        AuthError(0, 0, "Fake Error")

    override suspend fun performTokenRequest(tokenRequest: AuthTokenRequest): AuthTokenResponse =
        AuthTokenResponse(tokenRequest = tokenRequest)

    override fun createTokenExchangeRequest(authResponse: AuthAuthResponse) =
        AuthTokenRequest(
            config = fakeConfig,
            clientId = "000000000000000000000",
            grantType = "fake_auth_exchange"
        )

    override fun createTokenRefreshRequest(
        config: AuthServiceConfig,
        authState: AuthState
    ): AuthTokenRequest =
        AuthTokenRequest(
            config = fakeConfig,
            clientId = "000000000000000000000",
            grantType = "fake_token_refresh"
        )
}