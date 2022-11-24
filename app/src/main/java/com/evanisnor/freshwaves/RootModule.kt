package com.evanisnor.freshwaves

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object RootModule {

  @Qualifier
  @Retention(AnnotationRetention.RUNTIME)
  annotation class ApplicationSignature

  @Provides
  @ApplicationSignature
  fun packageManager(@ApplicationContext context: Context) =
    context.packageManager.getAppSignature(context.packageName)
}
