package com.evanisnor.freshwaves.backend

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistList(
  val artists: List<String>,
)
