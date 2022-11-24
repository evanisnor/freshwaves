package com.evanisnor.freshwaves.backend

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BackendAPIURLModule {

  @Provides
  @BackendAPIModule.BackendUrl
  fun backendUrl(): String = "https://freshwaves.evanisnor.com/"
}
