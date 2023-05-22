package com.evanisnor.freshwaves.features.notification.notificationmanager

import android.Manifest
import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.evanisnor.freshwaves.system.hasPermission
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationManagerDelegateModule {

  @Singleton
  @Binds
  abstract fun bindNotificationManagerDelegate(impl: RealNotificationManagerDelegate): NotificationManagerDelegate
}

/**
 * Delegate for de-coupling app code from [NotificationManagerCompat]
 */
interface NotificationManagerDelegate {

  fun createNotificationChannel(channel: NotificationChannelCompat)

  fun notify(id: Int, notification: Notification)
}

/**
 * Real implementation of [NotificationManagerDelegate] to bridge to [NotificationManagerCompat]
 */
class RealNotificationManagerDelegate @Inject constructor(
  @ApplicationContext private val context: Context,
  private val notificationManager: NotificationManagerCompat,
) : NotificationManagerDelegate {

  override fun createNotificationChannel(channel: NotificationChannelCompat) {
    notificationManager.createNotificationChannel(channel)
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun notify(id: Int, notification: Notification) {
    if (context.hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
      notificationManager.notify(id, notification)
    }
  }
}
