package com.evanisnor.freshwaves.features.freshalbums

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.fakes.DataFactory.Companion.quickAlbum
import com.evanisnor.freshwaves.fakes.DataFactory.Companion.quickArtist
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.tools.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FreshAlbumsTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var spotifyCacheDao: SpotifyCacheDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun freshAlbumsAreDisplayed() {
        spotifyCacheDao.insertArtists(artists.values.toList())
        spotifyCacheDao.insertAlbums(albums.values.toList())

        launchFragmentInHiltContainer<FreshAlbumsFragment> {}

        onView(withId(R.id.freshAlbumList)).check(matches(isDisplayed()))
    }

    companion object {

        val artists = mapOf(
            quickArtist("Superband"),
            quickArtist("Megadude and the Rockers")
        )

        val albums = mapOf(
            quickAlbum(
                id = 0,
                artist = artists["Superband"]!!,
                name = "The First One",
                releaseDate = 1633038133580
            ),
            quickAlbum(
                id = 1,
                artist = artists["Megadude and the Rockers"]!!,
                name = "This ain't so bad",
                releaseDate = 1633038123580
            ),
            quickAlbum(
                id = 2,
                artist = artists["Megadude and the Rockers"]!!,
                name = "This one could probably be better",
                releaseDate = 1633038113580
            )
        )
    }
}