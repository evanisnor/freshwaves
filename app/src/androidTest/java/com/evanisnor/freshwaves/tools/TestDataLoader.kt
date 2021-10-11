package com.evanisnor.freshwaves.tools

import android.content.Context
import android.util.Log
import com.evanisnor.freshwaves.spotify.network.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.source
import java.io.File
import kotlin.reflect.KClass

/**
 * Defined types of test data
 */
enum class TestData(val base: KClass<*>, vararg val parameterized: KClass<*>) {
    User(PrivateUserObject::class),
    Artists(PagingObject::class, ArtistObject::class),
    Albums(PagingObject::class, AlbumObject::class),
    Tracks(PagingObject::class, TrackObject::class)
}

/**
 * Loads JSON files from subfolders in the assets directory and parses them into objects for testing.
 */
class TestDataLoader(
    private val context: Context,
    private val moshi: Moshi = Moshi.Builder().build()
) {


    fun <T> loadData(testData: TestData, onRead: (T) -> Unit) {
        val adapter = if (testData.base == PagingObject::class) {
            moshi.adapter<T>(
                Types.newParameterizedType(
                    testData.base.java,
                    *(testData.parameterized.map { it.java }.toTypedArray())
                )
            )
        } else {
            moshi.adapter<T>(testData.base.java)
        }

        read(testData.name.lowercase(), adapter) {
            onRead(it)
        }
    }

    private fun <T> read(
        directory: String,
        adapter: JsonAdapter<T>,
        onRead: (T) -> Unit
    ) {
        with(context.assets) {
            list(directory)?.forEach { filename ->
                val path = "$directory${File.separator}$filename"
                Log.i("TestDataLoader", "Reading file: $path")
                open(path).use { inputStream ->
                    adapter.fromJson(inputStream.source().buffer())?.let { data ->
                        onRead(data)
                    }
                }
            }
        }
    }

}