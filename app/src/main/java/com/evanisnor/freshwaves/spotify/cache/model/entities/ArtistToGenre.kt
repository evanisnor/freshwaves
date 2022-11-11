package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
  primaryKeys = ["artistId", "genreId"],
  foreignKeys = [
    androidx.room.ForeignKey(
      entity = Artist::class,
      parentColumns = ["id"],
      childColumns = ["artistId"]
    ),
    androidx.room.ForeignKey(
      entity = ArtistGenre::class,
      parentColumns = ["id"],
      childColumns = ["genreId"]
    )
  ],
  indices = [
    Index(value = ["artistId"]),
    Index(value = ["genreId"])
  ]
)
data class ArtistToGenre(
  val artistId: String,
  val genreId: Long,
)
