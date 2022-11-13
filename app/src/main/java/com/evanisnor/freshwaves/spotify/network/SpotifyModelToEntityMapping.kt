package com.evanisnor.freshwaves.spotify.network

import android.net.Uri
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.AlbumImage
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistGenre
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistImage
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import com.evanisnor.freshwaves.spotify.network.model.AlbumObject
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.spotify.network.model.TrackObject
import com.evanisnor.freshwaves.user.UserProfile
import java.time.Duration
import java.time.Instant

fun PrivateUserObject.mapToUserProfile() = UserProfile(
  id = id,
  name = displayName,
  email = email,
  country = country,
)

@JvmName("mapToEntitiesArtistObject")
fun Collection<ArtistObject>.mapToEntity() = map(ArtistObject::mapToEntity)
fun ArtistObject.mapToEntity() = Artist(
  id = id,
  name = name,
  images?.map { imageObject ->
    ArtistImage(
      url = imageObject.url,
      artistId = id,
      width = imageObject.width,
      height = imageObject.height,
    )
  } ?: emptyList(),
  genres?.map { genre ->
    ArtistGenre(
      name = genre,
    )
  } ?: emptyList(),
)

@JvmName("mapToEntitiesAlbumObject")
fun Collection<AlbumObject>.mapToEntity(artistId: String) = map { albumObject ->
  val artist = albumObject.artists.first { it.id == artistId }.mapToEntity()
  albumObject.mapToEntity(artist)
}

@JvmName("mapToEntitiesAlbumObject")
fun Collection<AlbumObject>.mapToEntity(artist: Artist) = map { it.mapToEntity(artist) }
fun AlbumObject.mapToEntity(artist: Artist): Album {
  val albumId = "${artist.name} - ${name.filter { it.isLetterOrDigit() }}"
    .lowercase()
    .hashCode()

  return Album(
    id = albumId,
    spotifyId = id,
    artist = artist,
    name = name,
    type = type,
    releaseDate = when {
      releaseDate == null -> {
        Instant.MIN
      }
      releaseDatePrecision == "day" -> {
        Instant.parse("${releaseDate}T00:00:00Z")
      }
      releaseDatePrecision == "year" -> {
        Instant.parse("$releaseDate-01-01T00:00:00Z")
      }
      else -> {
        Instant.MIN
      }
    },
    spotifyUri = Uri.parse(externalUrls.spotify),
    images?.map { imageObject ->
      AlbumImage(
        url = imageObject.url,
        albumId = albumId,
        width = imageObject.width,
        height = imageObject.height,
      )
    } ?: emptyList(),
    tracks?.map { trackObject ->
      trackObject.mapToEntity(albumId)
    } ?: emptyList(),
  )
}

fun Collection<TrackObject>.mapToEntities(albumId: Int) = map { it.mapToEntity(albumId) }
fun TrackObject.mapToEntity(albumId: Int): Track {
  val trackId = "$albumId - $discNumber - $trackNumber - ${name.filter { it.isLetterOrDigit() }}"
    .lowercase()
    .hashCode()

  return Track(
    id = trackId,
    spotifyId = id,
    albumId = albumId,
    discNumber = discNumber,
    trackNumber = trackNumber,
    name = name,
    uri = uri,
    duration = Duration.ofMillis(durationMs),
  )
}
