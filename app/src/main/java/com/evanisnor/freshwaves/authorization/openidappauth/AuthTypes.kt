package com.evanisnor.freshwaves.authorization.openidappauth

import com.evanisnor.freshwaves.authorization.*
import net.openid.appauth.*

fun AuthServiceConfig.toOpenIdAuthConfig() = AuthorizationServiceConfiguration(
    this.authorizationEndpoint,
    this.tokenEndpoint
)

fun AuthAuthRequest.toOpenIdAuthRequest() =
    AuthorizationRequest.Builder(
        this.config.toOpenIdAuthConfig(),
        this.clientId,
        this.responseType,
        this.redirectUri
    )
        .setScope(this.scope)
        .build()

fun AuthAuthRequest.toOpenIdAuthRequestWithCodeVerifier() =
    AuthorizationRequest.Builder(
        this.config.toOpenIdAuthConfig(),
        this.clientId,
        this.responseType,
        this.redirectUri
    )
        .setScope(this.scope)
        .setCodeVerifier(
            this.codeVerifier,
            this.codeVerifierChallenge,
            this.codeVerifierChallengeMethod
        )
        .setNonce(this.nonce)
        .setState(this.state)
        .build()

fun AuthAuthResponse.toOpenIdAuthResponse() =
    AuthorizationResponse.Builder(this.request.toOpenIdAuthRequestWithCodeVerifier())
        .setState(this.state)
        .setTokenType(this.tokenType)
        .setAuthorizationCode(this.authorizationCode)
        .setAccessToken(this.accessToken)
        .setAccessTokenExpirationTime(this.accessTokenExpirationTime)
        .setIdToken(this.idToken)
        .setScope(this.scope)
        .setAdditionalParameters(this.additionalParameters)
        .build()

fun AuthTokenRequest.toOpenIdTokenRequest() =
    TokenRequest.Builder(this.config.toOpenIdAuthConfig(), this.clientId)
        .setNonce(this.nonce)
        .setGrantType(this.grantType)
        .setRedirectUri(this.redirectUri)
        .setScope(this.scope)
        .setAuthorizationCode(this.authorizationCode)
        .setRefreshToken(this.refreshToken)
        .setCodeVerifier(this.codeVerifier)
        .setAdditionalParameters(this.additionalParams)
        .build()


fun AuthTokenResponse.toOpenIdTokenResponse(request: AuthTokenRequest): TokenResponse =
    TokenResponse.Builder(request.toOpenIdTokenRequest())
        .setTokenType(this.tokenType)
        .setAccessToken(this.accessToken)
        .setAccessTokenExpirationTime(this.accessTokenExpirationTime)
        .setIdToken(this.idToken)
        .setRefreshToken(this.refreshToken)
        .setScope(this.scope)
        .setAdditionalParameters(this.additionalParameters)
        .build()


fun AuthError.toOpenIdAuthException() = AuthorizationException(
    this.type,
    this.code,
    this.error,
    this.errorDescription,
    this.errorUri,
    this.rootCause
)