package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.spotify.cache.model.entities.*
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class DataFactory {

    companion object {

        fun quickArtist(name: String) = Pair(
            name, Artist(
                id = name,
                name = name,
                images = listOf(
                    ArtistImage(
                        url = "https://image.png",
                        artistId = name,
                        width = 200,
                        height = 200
                    )
                ),
                genres = listOf(
                    ArtistGenre(
                        id = 0,
                        name = "Genre A"
                    )
                )
            )
        )

        fun quickAlbum(
            id: Int,
            artist: Artist,
            name: String,
            releaseDate: Long,
            tracks: List<Track> = listOf()
        ) = Pair(
            id, Album(
                id = id,
                spotifyId = "$id",
                artist = artist,
                name = name,
                type = "album",
                releaseDate = Instant.ofEpochMilli(releaseDate),
                images = listOf(
                    AlbumImage(
                        url = "https://image.png",
                        albumId = 0,
                        width = 200,
                        height = 200
                    )
                ),
                tracks = tracks
            )
        )

        fun quickTrack(
            id: Int,
            albumId: Int,
            trackNumber: Int,
            name: String,
            discNumber: Int,
            durationSec: Long
        ) = Track(
            id = id,
            spotifyId = "$id",
            albumId = albumId,
            discNumber = discNumber,
            trackNumber = trackNumber,
            name = name,
            uri = "$id",
            duration = Duration.of(durationSec, ChronoUnit.SECONDS)
        )

    }
}