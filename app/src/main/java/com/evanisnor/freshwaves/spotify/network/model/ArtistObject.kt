package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistObject(
  val id: String,
  val name: String,
  val images: List<ImageObject>? = null,
  val genres: List<String>? = null,
)
