package com.evanisnor.freshwaves.features.freshalbums

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.tools.RecyclerViewUtils.Companion.atPositionOnView
import com.evanisnor.freshwaves.tools.RecyclerViewUtils.Companion.scrollToPosition
import com.evanisnor.freshwaves.tools.TestDataLoader
import com.evanisnor.freshwaves.tools.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
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
        val context = ApplicationProvider.getApplicationContext<Context>()
        hiltRule.inject()

        with(TestDataLoader(context)) {
            loadAllRelationally(
                onArtists = { spotifyCacheDao.insertArtists(it) },
                onAlbums = { spotifyCacheDao.insertAlbums(it) },
                onTracks = { spotifyCacheDao.insertTracks(it) }
            )
        }
    }

    @Test
    fun freshAlbumsAreDisplayed() {
        launchFragmentInHiltContainer<FreshAlbumsFragment> {}

        val albums = spotifyCacheDao.readAlbumsWithLimit(30)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())

        albums.forEachIndexed { index, album ->
            onView(withId(R.id.freshAlbumList))
                .perform(scrollToPosition(index))
                .check(matches(atPositionOnView(index, R.id.albumName, withText(album.name))))
                .check(
                    matches(atPositionOnView(index, R.id.artistName, withText(album.artist!!.name)))
                )
                .check(
                    matches(
                        atPositionOnView(
                            index, R.id.albumImage, withTagValue(`is`(album.images.first().url))
                        )
                    )
                )
                .check(
                    matches(
                        atPositionOnView(
                            index, R.id.releaseDate, withText(
                                album.releaseDate.atZone(ZoneId.systemDefault()).toLocalDate()
                                    .format(dateTimeFormatter)
                            )
                        )
                    )
                )
        }
    }

    @Test
    fun clickFreshAlbumLaunchesAlbumDetails() {
        launchFragmentInHiltContainer<FreshAlbumsFragment> {}

        val albums = spotifyCacheDao.readAlbumsWithLimit(30)

        albums.forEachIndexed { index, _ ->
            onView(withId(R.id.freshAlbumList))
                .perform(scrollToPosition(index))
                .perform(atPositionOnView(index, click()))

            onView(withId(R.id.details)).check(matches(isDisplayed()))

            pressBack()
        }
    }

}