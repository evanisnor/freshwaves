package com.evanisnor.freshwaves.authorization.openidappauth

import com.evanisnor.freshwaves.authorization.*
import net.openid.appauth.TokenResponse
import net.openid.appauth.AuthState as OpenIdAuthState

class OpenIdAuthState(
    private val config: AuthServiceConfig,
    internal val authState: OpenIdAuthState = OpenIdAuthState(config.toOpenIdAuthConfig())
) : AuthState {

    constructor(
        config: AuthServiceConfig,
        authStateJson: String
    ) : this(
        config,
        OpenIdAuthState.jsonDeserialize(authStateJson)
    )

    override val isAuthorized: Boolean
        get() = authState.isAuthorized

    override var needsTokenRefresh: Boolean
        get() = authState.needsTokenRefresh
        set(value) {
            authState.needsTokenRefresh = value
        }

    override val accessToken: String?
        get() = authState.accessToken

    override fun update(authTokenRequest: AuthTokenRequest, authTokenResponse: AuthTokenResponse) {
        val response = authTokenResponse.toOpenIdTokenResponse(authTokenRequest)
        authState.update(response, null)
    }

    override fun update(authError: AuthError) {
        val exception = authError.toOpenIdAuthException()
        val tokenResponse: TokenResponse? = null
        authState.update(tokenResponse, exception)
    }

    override fun toJson(): String = authState.jsonSerializeString()
}