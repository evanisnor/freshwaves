package com.evanisnor.freshwaves.system

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.evanisnor.freshwaves.getAppSignature
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {

  @Qualifier
  @Retention(AnnotationRetention.RUNTIME)
  annotation class ApplicationSignature

  @Provides
  @ApplicationSignature
  fun packageManager(@ApplicationContext context: Context) =
    context.packageManager.getAppSignature(context.packageName)

  @Provides
  fun notificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
    NotificationManagerCompat.from(context)

  @Provides
  fun moshi(): Moshi = Moshi.Builder().build()
}
