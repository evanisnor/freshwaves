package com.evanisnor.freshwaves.authorization

data class AuthAuthResponse(
    val request: AuthAuthRequest,
    val state: String? = null,
    val tokenType: String? = null,
    val authorizationCode: String? = null,
    val accessToken: String? = null,
    val accessTokenExpirationTime: Long? = null,
    val idToken: String? = null,
    val scope: String? = null,
    val additionalParameters: Map<String, String> = emptyMap()
)