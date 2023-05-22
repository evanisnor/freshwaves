package com.evanisnor.freshwaves.features.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.evanisnor.freshwaves.MainActivity
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.features.updater.UpdaterRepository
import com.evanisnor.freshwaves.features.updater.startOfDayUTC
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.system.hasPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class FreshAlbumNotifier @Inject constructor(
  @ApplicationContext private val context: Context,
  private val notificationManager: NotificationManagerCompat,
  private val updaterRepository: UpdaterRepository,
  private val spotifyRepository: SpotifyRepository,
) {

  companion object {
    private const val NOTIFICATION_CHANNEL = "fresh-waves-update"
    private const val FRESH_ALBUMS_NOTIFICATION = 235246
  }

  private val imageLoader = ImageLoader.invoke(context)

  suspend fun notifyOfNewAlbums() {
    // Check for new albums released since the day after the last update, or just check from today.
    val checkDate = updaterRepository.lastRunOn()?.plus(1, ChronoUnit.DAYS) ?: Instant.now()
    val freshAlbums = spotifyRepository.getAlbumsReleasedAfter(checkDate.startOfDayUTC())
    send(freshAlbums)
  }

  suspend fun send(freshAlbums: List<Album>) = withContext(Dispatchers.Main) {
    Timber.i("Notifying user of fresh albums")
    freshAlbums.forEach { album ->
      val albumNotification = buildAlbumNotification(album)
      send(album, albumNotification)
    }

    if (freshAlbums.size > 1) {
      val messageNotification = buildMessageNotification(freshAlbums)
      send(messageNotification)
    }
  }

  fun createNotificationChannel() {
    val name = context.getString(R.string.notification_channel_name)
    val description = context.getString(R.string.notification_channel_description)
    val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT

    val channel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL, importance).apply {
      setName(name)
      setDescription(description)
    }.build()

    notificationManager.createNotificationChannel(channel)
  }

  private fun buildMessageNotification(albums: List<Album>): Notification {
    val title = context.resources.getQuantityString(
      R.plurals.notification_fresh_albums_title,
      albums.size,
      albums.size,
    )

    val text = context.getString(R.string.notification_fresh_albums_text)

    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
      .setSmallIcon(R.drawable.ic_wave_notification)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentTitle(title)
      .setContentText(text)
      .setContentIntent(launchMainActivityPendingIntent())
      .setAutoCancel(true)
      .build()
  }

  private suspend fun buildAlbumNotification(album: Album): Notification {
    val unknownArtist = context.getString(R.string.notification_unknown_artist)

    val title = context.getString(
      R.string.notification_fresh_album_title,
      album.name,
      album.artist?.name ?: unknownArtist,
    )

    val text = context.resources.getQuantityString(
      R.plurals.notification_fresh_album_text,
      album.tracks.size,
      album.tracks.size,
    )

    val albumImage = if (album.images.isNotEmpty()) {
      fetchAlbumImage(album)
    } else {
      null
    }

    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
      .setSmallIcon(R.drawable.disc_icon)
      .setLargeIcon(albumImage)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentTitle(title)
      .setContentText(text)
      .setContentIntent(launchAlbumDetailsPendingIntent(album.id))
      .setAutoCancel(true)
      .build()
  }

  private fun send(notification: Notification) {
    withNotificationPermission {
      //noinspection MissingPermission
      notificationManager.notify(FRESH_ALBUMS_NOTIFICATION, notification)
    }
  }

  private fun send(album: Album, notification: Notification) =
    withNotificationPermission {
      //noinspection MissingPermission
      notificationManager.notify(album.hashCode(), notification)
    }

  private fun launchMainActivityPendingIntent(): PendingIntent =
    PendingIntent.getActivity(
      context,
      0,
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun launchAlbumDetailsPendingIntent(albumId: Int): PendingIntent =
    PendingIntent.getActivity(
      context,
      0,
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra(MainActivity.extraAlbumId, albumId)
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private suspend fun fetchAlbumImage(album: Album): Bitmap? {
    val albumImageResult = imageLoader.execute(
      ImageRequest.Builder(context).apply {
        data(album.images.first().url)
      }.build(),
    )

    return if (albumImageResult is SuccessResult) {
      (albumImageResult.drawable as BitmapDrawable).bitmap
    } else {
      null
    }
  }

  private fun withNotificationPermission(ifGranted: () -> Unit) {
    if (context.hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
      ifGranted()
    }
  }
}
