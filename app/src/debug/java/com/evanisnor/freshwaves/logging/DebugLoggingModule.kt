package com.evanisnor.freshwaves.logging

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object DebugLoggingModule {

  @Provides
  @ElementsIntoSet
  fun debugTrees(): Set<Timber.Tree> = setOf(Timber.DebugTree())

}