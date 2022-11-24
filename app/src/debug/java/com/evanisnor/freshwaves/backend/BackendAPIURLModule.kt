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
  fun backendUrl(): String = "http://192.168.2.27:8080/"
}
