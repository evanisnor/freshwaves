package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.cache.model.entities.*
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
    country = country
)

@JvmName("mapToEntitiesArtistObject")
fun List<ArtistObject>.mapToEntity() = map(ArtistObject::mapToEntity)
fun ArtistObject.mapToEntity() = Artist(
    id = id,
    name = name,
    images?.map { imageObject ->
        ArtistImage(
            url = imageObject.url,
            artistId = id,
            width = imageObject.width,
            height = imageObject.height
        )
    } ?: emptyList(),
    genres?.map { genre ->
        ArtistGenre(
            name = genre
        )
    } ?: emptyList()
)

@JvmName("mapToEntitiesAlbumObject")
fun List<AlbumObject>.mapToEntity(artist: Artist) = map { it.mapToEntity(artist) }
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
                Instant.parse("${releaseDate}-01-01T00:00:00Z")
            }
            else -> {
                Instant.MIN
            }
        },
        images?.map { imageObject ->
            AlbumImage(
                url = imageObject.url,
                albumId = albumId,
                width = imageObject.width,
                height = imageObject.height
            )
        } ?: emptyList(),
        tracks?.map { trackObject ->
            trackObject.mapToEntity(albumId)
        } ?: emptyList()
    )
}

fun List<TrackObject>.mapToEntities(albumId: Int) = map { it.mapToEntity(albumId) }
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
        duration = Duration.ofMillis(durationMs)
    )
}