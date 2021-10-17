package com.evanisnor.freshwaves.spotify.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.evanisnor.freshwaves.spotify.cache.model.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Suppress("FunctionName")
@Dao
abstract class SpotifyCacheDao {

    // region Albums

    suspend fun readAlbumsWithImages(limit: Int) =
        _readAlbumsActive(limit).map { albums ->
            albums.forEach { album ->
                album.apply {
                    artist = _readArtist(artistId)
                    images = _readAlbumImages(id)
                }
            }
            albums
        }.flowOn(Dispatchers.Default)

    suspend fun readLatestAlbumsMissingTracks(limit: Int): List<Album> =
        withContext(Dispatchers.Default) {
            _readAlbums(limit)
                .filter { album -> _countTracks(album.id) == 0 }
                .map { album ->
                    album.apply {
                        artist = _readArtist(artistId)
                    }
                }
        }

    suspend fun readAlbumsWithImagesSync(limit: Int): List<Album> =
        withContext(Dispatchers.Default) {
            _readAlbums(limit).map { album ->
                album.apply {
                    artist = _readArtist(artistId)
                    images = _readAlbumImages(id)
                }
            }
        }

    suspend fun readAlbumWithTracks(albumId: Int): Album = withContext(Dispatchers.Default) {
        _readAlbum(albumId).let { album ->
            album.apply {
                artist = _readArtist(album.artistId)
                images = _readAlbumImages(album.id)
                tracks = _readTracks(album.id)
            }
        }
    }

    fun insertAlbums(albums: Collection<Album>) {
        albums.forEach { album ->

            _insertAlbum(album)

            album.images.forEach(this::_insertAlbumImage)
            album.tracks.forEach(this::_insertTrack)
        }
    }

    // endregion

    // region Artists

    suspend fun readArtists(): List<Artist> = withContext(Dispatchers.Default) {
        _readArtists()
    }

    @Query("SELECT * FROM Artist")
    abstract suspend fun _readArtists(): List<Artist>

    fun insertArtists(artists: Collection<Artist>) {
        artists.forEach(this::insertArtist)
    }

    private fun insertArtist(artist: Artist) {
        _insertArtist(artist)

        artist.genres.forEach { genre ->
            val genreId = _insertArtistGenre(genre)

            _insertArtistToGenre(
                ArtistToGenre(
                    artistId = artist.id,
                    genreId = genreId
                )
            )
        }

        artist.images.forEach { artistImage ->
            artistImage.artistId = artist.id
            _insertArtistImage(artistImage)
        }
    }

    // endregion

    // region Tracks

    fun insertTracks(tracks: Collection<Track>) {
        tracks.forEach {
            _insertTrack(it)
        }
    }

    // endregion

    // region Internal Read Methods

    @Query("SELECT * FROM Artist WHERE Artist.id = :artistId")
    abstract suspend fun _readArtist(artistId: String): Artist

    @Query(
        "SELECT * FROM Artist, Album" +
                " WHERE Artist.id = :artistId" +
                " AND Album.artistId = Artist.id" +
                " ORDER BY releaseDate DESC"
    )
    abstract fun _readAlbums(artistId: String): Flow<List<Album>>

    @Query(
        "SELECT * FROM Artist, Album" +
                " WHERE Album.artistId = Artist.id" +
                " ORDER BY releaseDate DESC" +
                " LIMIT :limit"
    )
    abstract suspend fun _readAlbums(limit: Int): List<Album>

    @Query(
        "SELECT * FROM Artist, Album" +
                " WHERE Album.artistId = Artist.id" +
                " ORDER BY releaseDate DESC" +
                " LIMIT :limit"
    )
    abstract fun _readAlbumsActive(limit: Int): Flow<List<Album>>

    @Query("SELECT * FROM Album WHERE Album.id = :albumId")
    abstract suspend fun _readAlbum(albumId: Int): Album

    @Query("SELECT * FROM AlbumImage WHERE albumId = :albumId")
    abstract suspend fun _readAlbumImages(albumId: Int): List<AlbumImage>

    @Query("SELECT * FROM Track WHERE Track.albumId = :albumId ORDER BY discNumber,trackNumber")
    abstract suspend fun _readTracks(albumId: Int): List<Track>

    @Query("SELECT count(*) FROM Track WHERE Track.albumId = :albumId")
    abstract suspend fun _countTracks(albumId: Int): Int

    // endregion

    // region Internal Insert Methods

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun _insertAlbumImage(image: AlbumImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun _insertArtistImage(image: ArtistImage)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun _insertArtistGenre(artistGenre: ArtistGenre): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun _insertArtistToGenre(artistToGenre: ArtistToGenre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertTrack(track: Track)

    // endregion


}