package com.evanisnor.freshwaves.system

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SystemModule {

  @Provides
  fun notificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
    NotificationManagerCompat.from(context)

  @Provides
  fun moshi(): Moshi = Moshi.Builder().build()
}
