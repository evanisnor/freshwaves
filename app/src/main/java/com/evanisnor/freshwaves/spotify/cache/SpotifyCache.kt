package com.evanisnor.freshwaves.spotify.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.evanisnor.freshwaves.spotify.cache.model.RoomTypeConverters
import com.evanisnor.freshwaves.spotify.cache.model.entities.*

@Database(
    version = 1,
    entities = [
        Album::class,
        AlbumImage::class,
        Artist::class,
        ArtistGenre::class,
        ArtistImage::class,
        ArtistToGenre::class,
        Track::class,
    ]
)
@TypeConverters(RoomTypeConverters::class)
abstract class SpotifyCache : RoomDatabase() {
    abstract fun spotifyCacheDao(): SpotifyCacheDao
}