package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.authorization.AuthError
import com.evanisnor.freshwaves.authorization.AuthState
import com.evanisnor.freshwaves.authorization.AuthTokenRequest
import com.evanisnor.freshwaves.authorization.AuthTokenResponse

class FakeAuthState : AuthState {

    override val isAuthorized: Boolean = true
    override val needsTokenRefresh: Boolean = false
    override val accessToken: String = "abcd1234"
    override val refreshToken: String = "efgh5678"

    override fun update(authTokenRequest: AuthTokenRequest, authTokenResponse: AuthTokenResponse) {
    }

    override fun update(authError: AuthError) {
    }

    override fun toJson(): String = "{}"
}