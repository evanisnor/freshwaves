package com.evanisnor.freshwaves.fakes

import android.content.Context
import com.evanisnor.freshwaves.authorization.AuthService
import com.evanisnor.freshwaves.authorization.AuthServiceConfig
import com.evanisnor.freshwaves.authorization.AuthState
import com.evanisnor.freshwaves.authorization.OpenIdAppAuthModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [OpenIdAppAuthModule::class]
)
object FakeAuthorizationModule {

    @Provides
    fun authStateFactory(): AuthState.Factory {
        val fakeAuthState = FakeAuthState()

        return object : AuthState.Factory {
            override fun create(config: AuthServiceConfig): AuthState {
                return fakeAuthState
            }

            override fun create(config: AuthServiceConfig, json: String): AuthState {
                return fakeAuthState
            }
        }
    }


    @Provides
    fun authorizationServiceFactory(): AuthService.Factory =
        object : AuthService.Factory {
            override fun create(context: Context): AuthService {
                return FakeAuthService()
            }
        }

}