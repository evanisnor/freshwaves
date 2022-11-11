package com.evanisnor.freshwaves.spotify.cache.model.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = Artist::class,
      parentColumns = ["id"],
      childColumns = ["artistId"]
    )
  ],
  indices = [
    Index(value = ["artistId"])
  ]
)
data class Album(
  @PrimaryKey val id: Int,
  val spotifyId: String,
  val artistId: String,
  val name: String,
  val type: String,
  val releaseDate: Instant,
  val spotifyUri: Uri,
) {

  @Ignore
  var artist: Artist? = null

  @Ignore
  var images: List<AlbumImage> = emptyList()

  @Ignore
  var tracks: List<Track> = emptyList()

  constructor(
    id: Int,
    spotifyId: String = id.toString(),
    artist: Artist,
    name: String,
    type: String = "album",
    releaseDate: Instant,
    spotifyUri: Uri = Uri.EMPTY,
    images: List<AlbumImage> = emptyList(),
    tracks: List<Track> = emptyList(),
  ) : this(id, spotifyId, artist.id, name, type, releaseDate, spotifyUri) {
    this.artist = artist
    this.images = images
    this.tracks = tracks
  }

  override fun toString() =
    if (artist == null) {
      "Album(id=$id, spotifyId=$spotifyId, name=$name, releaseDate=$releaseDate, artistId=$artistId, images=$images, tracks=$tracks)"
    } else {
      "Album(id=$id, spotifyId=$spotifyId, name=$name, releaseDate=$releaseDate, artist=$artist, images=$images, tracks=$tracks)"
    }
}

