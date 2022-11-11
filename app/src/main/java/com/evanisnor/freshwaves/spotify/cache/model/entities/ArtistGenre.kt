package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArtistGenre(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
)
