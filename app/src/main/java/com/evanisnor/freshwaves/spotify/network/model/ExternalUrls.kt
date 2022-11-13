package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalUrls(
  val spotify: String,
)
