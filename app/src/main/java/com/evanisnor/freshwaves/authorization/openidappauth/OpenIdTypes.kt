package com.evanisnor.freshwaves.authorization.openidappauth

import com.evanisnor.freshwaves.authorization.*
import net.openid.appauth.*


fun AuthorizationServiceConfiguration.toAuthServiceConfig() = AuthServiceConfig(
    this.authorizationEndpoint,
    this.tokenEndpoint
)

fun AuthorizationRequest.toAuthRequest() = AuthAuthRequest(
    this.configuration.toAuthServiceConfig(),
    this.clientId,
    this.responseType,
    this.redirectUri,
    this.scope ?: "",
    this.codeVerifier,
    this.codeVerifierChallenge,
    this.codeVerifierChallengeMethod,
    this.nonce,
    this.state
)

fun AuthorizationResponse.toAuthResponse() = AuthAuthResponse(
    this.request.toAuthRequest(),
    this.state,
    this.tokenType,
    this.authorizationCode,
    this.accessToken,
    this.accessTokenExpirationTime,
    this.idToken,
    this.scope,
    this.additionalParameters
)

fun TokenRequest.toAuthTokenRequest() = AuthTokenRequest(
    this.configuration.toAuthServiceConfig(),
    this.clientId,
    this.nonce,
    this.grantType,
    this.redirectUri,
    this.scope,
    this.authorizationCode,
    this.refreshToken,
    this.codeVerifier,
    this.additionalParameters
)

fun TokenResponse.toAuthTokenResponse(authTokenRequest: AuthTokenRequest) = AuthTokenResponse(
    authTokenRequest,
    this.tokenType,
    this.accessToken,
    this.accessTokenExpirationTime,
    this.idToken,
    this.refreshToken,
    this.scope,
    this.additionalParameters
)

fun AuthorizationException.toAuthError() = AuthError(
    this.type,
    this.code,
    this.error,
    this.errorDescription,
    this.errorUri,
    this.cause
)