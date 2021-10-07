package com.evanisnor.freshwaves.authorization.openidappauth

import com.evanisnor.freshwaves.authorization.*
import net.openid.appauth.TokenResponse
import net.openid.appauth.AuthState as ExternalAuthState

class OpenIdAuthState(
    private val config: AuthServiceConfig,
    internal val authState: ExternalAuthState = ExternalAuthState(config.toOpenIdAuthConfig())
) : AuthState {

    constructor(
        config: AuthServiceConfig,
        authStateJson: String
    ) : this(
        config,
        ExternalAuthState.jsonDeserialize(authStateJson)
    )

    override val isAuthorized: Boolean
        get() = authState.isAuthorized

    override val needsTokenRefresh: Boolean
        get() = authState.needsTokenRefresh

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