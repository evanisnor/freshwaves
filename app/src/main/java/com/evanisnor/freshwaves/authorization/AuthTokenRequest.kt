package com.evanisnor.freshwaves.authorization

import android.net.Uri

data class AuthTokenRequest(
    val config: AuthServiceConfig,
    val clientId: String,
    val nonce: String? = null,
    val grantType: String,
    val redirectUri: Uri? = null,
    val scope: String? = null,
    val authorizationCode: String? = null,
    val refreshToken: String? = null,
    val codeVerifier: String? = null,
    val additionalParams: Map<String, String> = emptyMap()
)