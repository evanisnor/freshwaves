package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["url", "artistId"],
    foreignKeys = [ForeignKey(
        entity = Artist::class,
        parentColumns = ["id"],
        childColumns = ["artistId"]
    )],
    indices = [
        Index(value = ["artistId"])
    ]
)
data class ArtistImage(
    val url: String,
    var artistId: String,
    val width: Int,
    val height: Int
)
