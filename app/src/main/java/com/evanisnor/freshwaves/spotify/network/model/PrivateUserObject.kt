package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PrivateUserObject(
    val id: String,
    val email: String,
    @field:Json(name = "display_name") val displayName: String,
    val country: String
)
