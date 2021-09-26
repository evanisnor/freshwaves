package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.Json

data class PrivateUserObject(
    val id: String,
    val email: String,
    @field:Json(name = "display_name") val displayName: String,
    val country: String
)
