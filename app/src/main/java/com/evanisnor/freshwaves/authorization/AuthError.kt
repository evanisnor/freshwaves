package com.evanisnor.freshwaves.authorization

import android.net.Uri

data class AuthError(
    val type: Int,
    val code: Int,
    val error: String? = null,
    val errorDescription: String? = null,
    val errorUri: Uri? = null,
    val rootCause: Throwable? = null
) : Throwable("[$error] $errorDescription", rootCause)