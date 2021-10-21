package com.evanisnor.freshwaves.spotify.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumObject(
    val id: String,
    val name: String,
    val type: String,
    @field:Json(name = "release_date") val releaseDate: String?,
    @field:Json(name = "release_date_precision") val releaseDatePrecision: String?,
    @field:Json(name = "external_urls") val externalUrls: ExternalUrls,
    val images: List<ImageObject>?,
    val artists: List<ArtistObject>,
    val tracks: List<TrackObject>?
)
