package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey val id: String,
    val name: String
) {

    @Ignore
    var images: List<ArtistImage> = emptyList()

    @Ignore
    var genres: List<ArtistGenre> = emptyList()

    constructor(
        id: String,
        name: String,
        images: List<ArtistImage>,
        genres: List<ArtistGenre>
    ) : this(id, name) {
        this.images = images
        this.genres = genres
    }

    override fun toString(): String {
        return "Artist(id=$id, name=$name, images=$images, genres=$genres)"
    }

}
