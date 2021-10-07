package com.evanisnor.freshwaves.authorization

import android.net.Uri

data class AuthAuthRequest(
    val config: AuthServiceConfig,
    val clientId: String,
    val responseType: String,
    val redirectUri: Uri,
    val scope: String,
    val codeVerifier: String? = null,
    val codeVerifierChallenge: String? = null,
    val codeVerifierChallengeMethod: String? = null,
    val nonce: String? = null,
    val state: String? = null,
    val responseMode: String? = null
)
