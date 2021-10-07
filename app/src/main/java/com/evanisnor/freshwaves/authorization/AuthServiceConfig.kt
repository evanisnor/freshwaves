package com.evanisnor.freshwaves.authorization

import android.net.Uri

data class AuthServiceConfig(
    val authorizationEndpoint: Uri,
    val tokenEndpoint: Uri
)
