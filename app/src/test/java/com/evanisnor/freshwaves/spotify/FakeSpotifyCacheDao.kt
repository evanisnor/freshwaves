package com.evanisnor.freshwaves.spotify

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.evanisnor.freshwaves.spotify.cache.SpotifyCache
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.AlbumImage
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistGenre
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistImage
import com.evanisnor.freshwaves.spotify.cache.model.entities.ArtistToGenre
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Suppress("TestFunctionName")
class FakeSpotifyCacheDao(
  context: Context = InstrumentationRegistry.getInstrumentation().context,
  private val dao: SpotifyCacheDao =
    Room.inMemoryDatabaseBuilder(
      context,
      SpotifyCache::class.java,
    ).allowMainThreadQueries().build().spotifyCacheDao(),
) : SpotifyCacheDao() {

  override suspend fun _readArtists(): List<Artist> = dao._readArtists()

  override suspend fun _readArtist(artistId: String): Artist = dao._readArtist(artistId)

  override fun _readAlbums(artistId: String): Flow<List<Album>> = dao._readAlbums(artistId)

  override suspend fun _readAlbums(limit: Int): List<Album> = dao._readAlbums(limit)

  override fun _readAlbumsActive(limit: Int): Flow<List<Album>> = dao._readAlbumsActive(limit)

  override suspend fun _readAlbumsReleasedAfter(instant: Instant): List<Album> =
    dao._readAlbumsReleasedAfter(instant)

  override suspend fun _readAlbum(albumId: Int): Album = dao._readAlbum(albumId)

  override suspend fun _readAlbumImages(albumId: Int): List<AlbumImage> =
    dao._readAlbumImages(albumId)

  override suspend fun _readTracks(albumId: Int): List<Track> = dao._readTracks(albumId)

  override suspend fun _countTracks(albumId: Int): Int = dao._countTracks(albumId)

  override fun _insertAlbum(album: Album) = dao._insertAlbum(album)

  override fun _insertAlbumImage(image: AlbumImage) = dao._insertAlbumImage(image)

  override fun _insertArtist(artist: Artist) = dao._insertArtist(artist)

  override fun _insertArtistImage(image: ArtistImage) = dao._insertArtistImage(image)

  override fun _insertArtistGenre(artistGenre: ArtistGenre): Long =
    dao._insertArtistGenre(artistGenre)

  override fun _insertArtistToGenre(artistToGenre: ArtistToGenre) =
    dao._insertArtistToGenre(artistToGenre)

  override fun _insertTrack(track: Track) = dao._insertTrack(track)
}
