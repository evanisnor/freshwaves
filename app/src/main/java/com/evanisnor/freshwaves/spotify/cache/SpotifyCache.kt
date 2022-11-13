package com.evanisnor.freshwaves.spotify.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.evanisnor.freshwaves.spotify.cache.model.RoomTypeConverters
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.AlbumImage
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistGenre
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistImage
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistToGenre
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track

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
  ],
  exportSchema = false,
)
@TypeConverters(RoomTypeConverters::class)
abstract class SpotifyCache : RoomDatabase() {
  abstract fun spotifyCacheDao(): SpotifyCacheDao
}
