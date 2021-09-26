package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity

@Entity(
    primaryKeys = ["id", "albumId", "trackNumber"],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"]
        )
    ]
)
data class Track(
    val id: String,
    val albumId: Int,
    val trackNumber: Int,
    val name: String,
    val uri: String,
    val duration: String,
)
