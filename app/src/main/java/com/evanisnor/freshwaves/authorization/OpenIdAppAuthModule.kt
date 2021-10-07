package com.evanisnor.freshwaves.authorization

import android.content.Context
import com.evanisnor.freshwaves.authorization.openidappauth.OpenIdAuthService
import com.evanisnor.freshwaves.authorization.openidappauth.OpenIdAuthState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object OpenIdAppAuthModule {

    @Provides
    fun authStateFactory(): AuthState.Factory =
        object : AuthState.Factory {
            override fun create(config: AuthServiceConfig): AuthState {
                return OpenIdAuthState(config)
            }

            override fun create(config: AuthServiceConfig, json: String): AuthState {
                return OpenIdAuthState(config, json)
            }
        }

    @Provides
    fun authorizationServiceFactory(): AuthService.Factory =
        object : AuthService.Factory {
            override fun create(context: Context): AuthService {
                return OpenIdAuthService(context)
            }
        }

}