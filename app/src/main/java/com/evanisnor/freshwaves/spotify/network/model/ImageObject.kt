package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageObject(
    val width: Int,
    val height: Int,
    val url: String
)
