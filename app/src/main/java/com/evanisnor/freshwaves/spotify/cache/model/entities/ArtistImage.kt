package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["url", "artistId"],
    foreignKeys = [ForeignKey(
        entity = Artist::class,
        parentColumns = ["id"],
        childColumns = ["artistId"]
    )]
)
data class ArtistImage(
    val url: String,
    var artistId: String,
    val width: Int,
    val height: Int
)
