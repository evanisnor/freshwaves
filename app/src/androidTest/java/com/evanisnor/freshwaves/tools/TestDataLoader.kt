package com.evanisnor.freshwaves.tools

import android.content.Context
import android.util.Log
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import com.evanisnor.freshwaves.spotify.network.mapToEntities
import com.evanisnor.freshwaves.spotify.network.mapToEntity
import com.evanisnor.freshwaves.spotify.network.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.source
import java.io.File
import java.io.IOException
import kotlin.reflect.KClass

/**
 * Defined types of test data
 */
enum class TestData(val base: KClass<*>, vararg val parameterized: KClass<*>) {
    User(PrivateUserObject::class),
    Artists(PagingObject::class, ArtistObject::class),
    Albums(PagingObject::class, AlbumObject::class),
    Tracks(PagingObject::class, TrackObject::class);

    val directory: String = name.lowercase()

    fun file(id: String) = "$directory${File.separator}$id.json"

    fun file(subdirectory: String, id: String) =
        "$directory${File.separator}$subdirectory${File.separator}$id.json"
}

/**
 * Loads JSON files from subfolders in the assets directory and parses them into objects for testing.
 */
class TestDataLoader(
    private val context: Context,
    private val moshi: Moshi = Moshi.Builder().build()
) {

    fun <T> loadAllByType(testData: TestData, onRead: (T) -> Unit) {
        val adapter = makeAdapter<T>(testData)
        readAll(testData.directory, adapter, onRead)
    }

    fun loadAllRelationally(
        onArtists: (Collection<Artist>) -> Unit,
        onAlbums: (Collection<Album>) -> Unit,
        onTracks: (Collection<Track>) -> Unit
    ) {
        val artistAdapter = makeAdapter<PagingObject<ArtistObject>>(TestData.Artists)
        val albumAdapter = makeAdapter<PagingObject<AlbumObject>>(TestData.Albums)
        val trackAdapter = makeAdapter<PagingObject<TrackObject>>(TestData.Tracks)

        readAll(TestData.Artists.directory, artistAdapter) { artistsPage ->

            val artists = artistsPage.items.mapToEntity()
            onArtists(artists)

            artists.forEach { artist ->
                val albumsPath = TestData.Albums.file(artist.id)
                read(
                    albumsPath,
                    albumAdapter,
                    onRead = { albumsPage ->

                        val albums = albumsPage.items.mapToEntity(artist)
                        onAlbums(albums)

                        albums.forEach { album ->
                            val tracksPath = TestData.Tracks.file(artist.id, "${album.id}")
                            read(
                                tracksPath,
                                trackAdapter,
                                onRead = { tracksPage ->
                                    onTracks(tracksPage.items.mapToEntities(album.id))
                                },
                                onFileDoesNotExist = {
                                    Log.e(
                                        "TestDataLoader",
                                        "Tracks file does not exist for album ${artist.name}, ${album.name} | $tracksPath"
                                    )
                                }
                            )
                        }
                    },
                    onFileDoesNotExist = {
                        Log.e(
                            "TestDataLoader",
                            "Albums file does not exist for artist ${artist.name} | $albumsPath"
                        )
                    }
                )
            }
        }

    }

    private fun <T> readAll(
        directory: String,
        adapter: JsonAdapter<T>,
        onRead: (T) -> Unit
    ) {
        with(context.assets) {
            list(directory)?.forEach { filename ->
                val path = "$directory${File.separator}$filename"
                read(path, adapter, onRead) {
                    Log.e("TestDataLoader", "File does not exist: $path")
                }
            }
        }
    }

    private fun <T> read(
        path: String,
        adapter: JsonAdapter<T>,
        onRead: (T) -> Unit,
        onFileDoesNotExist: () -> Unit
    ) {
        with(context.assets) {
            Log.i("TestDataLoader", "Reading file: $path")
            try {
                open(path).use { inputStream ->
                    adapter.fromJson(inputStream.source().buffer())?.let { data ->
                        onRead(data)
                    }
                }
            } catch (e: IOException) {
                onFileDoesNotExist()
            }
        }
    }

    private fun <T> makeAdapter(testData: TestData) = if (testData.parameterized.isNotEmpty()) {
        moshi.adapter<T>(
            Types.newParameterizedType(
                testData.base.java,
                *(testData.parameterized.map { it.java }.toTypedArray())
            )
        )
    } else {
        moshi.adapter<T>(testData.base.java)
    }

}