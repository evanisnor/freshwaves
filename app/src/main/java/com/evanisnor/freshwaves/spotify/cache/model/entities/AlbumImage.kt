package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = Album::class,
      parentColumns = ["id"],
      childColumns = ["albumId"]
    )
  ],
  indices = [
    Index(value = ["albumId"])
  ]
)
data class AlbumImage(
  @PrimaryKey val url: String,
  val albumId: Int,
  val width: Int,
  val height: Int,
)
