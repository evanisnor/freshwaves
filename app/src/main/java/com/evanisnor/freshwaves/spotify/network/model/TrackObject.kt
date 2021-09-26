package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.Json

data class TrackObject(
    val id: String,
    val name: String,
    @Json(name = "track_number") val trackNumber: Int,
    val uri: String,
    val duration: String,
    val artists: List<ArtistObject>

)
