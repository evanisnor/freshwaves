package com.evanisnor.freshwaves.authorization

data class AuthTokenResponse(
    val tokenRequest: AuthTokenRequest,
    val tokenType: String? = null,
    val accessToken: String? = null,
    val accessTokenExpirationTime: Long? = null,
    val idToken: String? = null,
    val refreshToken: String? = null,
    val scope: String? = null,
    val additionalParameters: Map<String, String> = emptyMap()
)