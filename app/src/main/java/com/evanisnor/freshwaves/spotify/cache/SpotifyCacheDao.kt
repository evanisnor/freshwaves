package com.evanisnor.freshwaves.spotify.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.evanisnor.freshwaves.spotify.cache.model.entities.*

@Suppress("FunctionName")
@Dao
abstract class SpotifyCacheDao {

    // region Albums

    fun readAlbumsWithLimit(limit: Int) = _readAlbums(limit).map {
        it.artist = _readArtist(it.artistId)
        it.images = _readAlbumImages(it.id)
        it
    }

    fun readAlbumsForArtist(artistId: String): List<Album> = _readAlbums(artistId).map {
        it.artist = _readArtist(it.artistId)
        it.images = _readAlbumImages(it.id)
        it
    }

    fun readAlbumWithTracks(albumId: Int): Album = _readAlbum(albumId).let {
        it.artist = _readArtist(it.artistId)
        it.images = _readAlbumImages(it.id)
        it.tracks = _readTracks(it.id)
        it
    }

    fun insertAlbums(albums: List<Album>) {
        albums.forEach { album ->

            _insertAlbum(album)

            album.images.forEach(this::_insertAlbumImage)
            album.tracks.forEach(this::_insertTrack)
        }
    }

    // endregion

    // region Artists

    @Query("SELECT * FROM Artist")
    abstract fun readArtists(): List<Artist>

    fun insertArtists(artists: List<Artist>) {
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

    fun insertTracks(tracks: List<Track>) {
        tracks.forEach {
            _insertTrack(it)
        }
    }

    // endregion

    // region Internal Read Methods

    @Query("SELECT * FROM Artist WHERE Artist.id = :artistId")
    abstract fun _readArtist(artistId: String): Artist

    @Query(
        "SELECT * FROM Artist, Album" +
                " WHERE Artist.id = :artistId" +
                " AND Album.artistId = Artist.id" +
                " ORDER BY releaseDate DESC"
    )
    abstract fun _readAlbums(artistId: String): List<Album>

    @Query(
        "SELECT * FROM Artist, Album" +
                " WHERE Album.artistId = Artist.id" +
                " ORDER BY releaseDate DESC" +
                " LIMIT :limit"
    )
    abstract fun _readAlbums(limit: Int): List<Album>

    @Query("SELECT * FROM Album WHERE Album.id = :albumId")
    abstract fun _readAlbum(albumId: Int): Album

    @Query("SELECT * FROM AlbumImage WHERE albumId = :albumId")
    abstract fun _readAlbumImages(albumId: Int): List<AlbumImage>

    @Query("SELECT * FROM Track WHERE Track.albumId = :albumId ORDER BY discNumber,trackNumber")
    abstract fun _readTracks(albumId: Int): List<Track>

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