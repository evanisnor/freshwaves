package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.spotify.auth.HandyAuthModule
import com.evanisnor.handyauth.client.HandyAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HandyAuthModule::class]
)
object FakeAuthorizationModule {

    @Provides
    fun handyAuth(): HandyAuth = FakeHandyAuth()

}