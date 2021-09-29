package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.Json

data class TrackObject(
    val id: String,
    val name: String,
    @field:Json(name="disc_number") val discNumber: Int,
    @field:Json(name = "track_number") val trackNumber: Int,
    val uri: String,
    @field:Json(name = "duration_ms") val durationMs: Long,
    val artists: List<ArtistObject>

)
