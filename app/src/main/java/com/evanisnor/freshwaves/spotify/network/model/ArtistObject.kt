package com.evanisnor.freshwaves.spotify.network.model

data class ArtistObject(
    val id: String,
    val name: String,
    val images: List<ImageObject>,
    val genres: List<String>
)
