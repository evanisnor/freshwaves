package com.evanisnor.freshwaves.spotify.cache.model

import android.net.Uri
import androidx.room.TypeConverter
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter

class RoomTypeConverters {

  @TypeConverter
  fun fromDateStringToInstant(dateString: String): Instant = Instant.parse(dateString)

  @TypeConverter
  fun fromInstantToDateString(instant: Instant): String =
    DateTimeFormatter.ISO_INSTANT.format(instant)

  @TypeConverter
  fun fromMillisecondsToDuration(duration: Long): Duration = Duration.ofMillis(duration)

  @TypeConverter
  fun fromDurationToMilliseconds(duration: Duration): Long = duration.toMillis()

  @TypeConverter
  fun fromUriStringToUri(uriString: String): Uri = Uri.parse(uriString)

  @TypeConverter
  fun fromUriToUriString(uri: Uri): String = uri.toString()
}
