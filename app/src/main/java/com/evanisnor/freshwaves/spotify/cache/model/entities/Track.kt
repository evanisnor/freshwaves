package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.Index
import java.time.Duration

@Entity(
    primaryKeys = ["id"],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"]
        )
    ],
    indices = [
        Index(value = ["albumId"])
    ]
)
data class Track(
    val id: Int,
    val spotifyId: String,
    val albumId: Int,
    val discNumber: Int,
    val trackNumber: Int,
    val name: String,
    val uri: String,
    val duration: Duration,
)
