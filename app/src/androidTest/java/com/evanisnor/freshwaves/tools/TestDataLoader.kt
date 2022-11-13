package com.evanisnor.freshwaves.tools

import android.content.Context
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import com.evanisnor.freshwaves.spotify.network.mapToEntities
import com.evanisnor.freshwaves.spotify.network.mapToEntity
import com.evanisnor.freshwaves.spotify.network.model.AlbumObject
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PagingObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.spotify.network.model.TrackObject
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File
import java.io.IOException
import kotlin.reflect.KClass
import okio.buffer
import okio.source
import timber.log.Timber

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
  private val moshi: Moshi = Moshi.Builder().build(),
) {

  fun <T> loadAllByType(testData: TestData, onRead: (T) -> Unit) {
    val adapter = makeAdapter<T>(testData)
    readAll(testData.directory, adapter, onRead)
  }

  fun loadEntitiesRelationally(
    onArtists: (Collection<Artist>) -> Unit,
    onAlbums: (Collection<Album>) -> Unit,
    onTracks: (Collection<Track>) -> Unit,
  ) {
    loadNetworkModelRelationally(
      onArtists = { artistPage ->
        onArtists(artistPage.items.mapToEntity())
      },
      onAlbums = { artist, albumsPage ->
        onAlbums(albumsPage.items.mapToEntity(artist.id))
      },
      onTracks = { _, albumEntity, tracksPage ->
        onTracks(tracksPage.items.mapToEntities(albumEntity.id))
      }
    )
  }

  fun loadNetworkModelRelationally(
    onArtists: (PagingObject<ArtistObject>) -> Unit,
    onAlbums: (ArtistObject, PagingObject<AlbumObject>) -> Unit,
    onTracks: (AlbumObject, Album, PagingObject<TrackObject>) -> Unit,
  ) {
    val artistAdapter = makeAdapter<PagingObject<ArtistObject>>(TestData.Artists)
    val albumAdapter = makeAdapter<PagingObject<AlbumObject>>(TestData.Albums)
    val trackAdapter = makeAdapter<PagingObject<TrackObject>>(TestData.Tracks)

    readAll(TestData.Artists.directory, artistAdapter) { artistsPage ->
      onArtists(artistsPage)
      artistsPage.items.forEach { artist ->
        val albumsPath = TestData.Albums.file(artist.id)
        read(
          albumsPath,
          albumAdapter,
          onRead = { albumsPage ->
            onAlbums(artist, albumsPage)

            albumsPage.items.forEach { album ->
              val albumEntity = album.mapToEntity(artist.mapToEntity())
              val tracksPath = TestData.Tracks.file(artist.id, "${albumEntity.id}")
              read(
                tracksPath,
                trackAdapter,
                onRead = { tracksPage ->
                  onTracks(album, albumEntity, tracksPage)
                },
                onFileDoesNotExist = {
                  Timber.e("Tracks file does not exist for album ${artist.name}, ${album.name} | $tracksPath")
                }
              )
            }
          },
          onFileDoesNotExist = {
            Timber.e("Albums file does not exist for artist ${artist.name} | $albumsPath")
          }
        )
      }
    }

  }

  private fun <T> readAll(
    directory: String,
    adapter: JsonAdapter<T>,
    onRead: (T) -> Unit,
  ) {
    with(context.assets) {
      list(directory)?.forEach { layer2file ->
        val path = "$directory${File.separator}$layer2file"
        val files = list(path)
        if (files != null && files.isNotEmpty()) {
          files.forEach { layer3file ->
            read(path + "${File.separator}$layer3file", adapter, onRead) {
              Timber.e("File does not exist: $path")
            }
          }
        } else {
          read(path, adapter, onRead) {
            Timber.e("File does not exist: $path")
          }
        }
      }
    }
  }

  private fun <T> read(
    path: String,
    adapter: JsonAdapter<T>,
    onRead: (T) -> Unit,
    onFileDoesNotExist: () -> Unit,
  ) {
    with(context.assets) {
      Timber.i("Reading file: $path")
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