package com.evanisnor.freshwaves.spotify.cache.model

import androidx.room.TypeConverter
import java.time.Instant
import java.time.format.DateTimeFormatter

class RoomTypeConverters {

    @TypeConverter
    fun fromDateStringToInstant(dateString: String): Instant = Instant.parse(dateString)

    @TypeConverter
    fun fromInstantToDateString(instant: Instant): String =
        DateTimeFormatter.ISO_INSTANT.format(instant)
}