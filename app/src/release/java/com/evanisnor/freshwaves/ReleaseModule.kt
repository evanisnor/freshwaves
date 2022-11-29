package com.evanisnor.freshwaves

import com.evanisnor.freshwaves.system.DebugMenu
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ReleaseModule {

  @Provides
  fun debugMenu(): DebugMenu = object : DebugMenu {}
}
