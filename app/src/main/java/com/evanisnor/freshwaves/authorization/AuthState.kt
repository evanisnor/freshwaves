package com.evanisnor.freshwaves.authorization

interface AuthState {

    interface Factory {
        fun create(config: AuthServiceConfig): AuthState
        fun create(config: AuthServiceConfig, json: String): AuthState
    }


    val isAuthorized: Boolean
    val needsTokenRefresh: Boolean
    val accessToken: String?

    fun update(authTokenRequest: AuthTokenRequest, authTokenResponse: AuthTokenResponse)

    fun update(authError: AuthError)

    fun toJson(): String

}