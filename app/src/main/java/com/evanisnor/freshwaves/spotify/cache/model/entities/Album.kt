package com.evanisnor.freshwaves.spotify.cache.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artistId"]
        )
    ]
)
data class Album(
    @PrimaryKey val id: Int,
    val artistId: String,
    val name: String,
    val type: String,
    val releaseDate: Instant
) {

    @Ignore
    var artist: Artist? = null

    @Ignore
    var images: List<AlbumImage> = emptyList()

    @Ignore
    var tracks: List<Track> = emptyList()

    constructor(
        id: Int,
        artist: Artist,
        name: String,
        type: String,
        releaseDate: Instant,
        images: List<AlbumImage>,
        tracks: List<Track>
    ) : this(id, artist.id, name, type, releaseDate) {
        this.artist = artist
        this.images = images
        this.tracks = tracks
    }

    override fun toString() =
        if (artist == null) {
            "Album(id=$id, name=$name, releaseDate=$releaseDate, artistId=$artistId, images=$images, tracks=$tracks)"
        } else {
            "Album(id=$id, name=$name, releaseDate=$releaseDate, artist=$artist, images=$images, tracks=$tracks)"
        }
}

