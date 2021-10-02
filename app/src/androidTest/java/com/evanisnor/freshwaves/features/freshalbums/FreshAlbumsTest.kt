package com.evanisnor.freshwaves.features.freshalbums

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.fakes.DataFactory.Companion.quickAlbum
import com.evanisnor.freshwaves.fakes.DataFactory.Companion.quickArtist
import com.evanisnor.freshwaves.features.albumdetails.AlbumDetailsFragment
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.tools.RecyclerViewUtils.Companion.atPositionOnView
import com.evanisnor.freshwaves.tools.RecyclerViewUtils.Companion.scrollToPosition
import com.evanisnor.freshwaves.tools.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
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
        hiltRule.inject()

        spotifyCacheDao.insertArtists(artists.values.toList())
        spotifyCacheDao.insertAlbums(albums.values.toList())
    }

    @Test
    fun freshAlbumsAreDisplayed() {
        launchFragmentInHiltContainer<FreshAlbumsFragment> {}

        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())

        albums.values.forEachIndexed { index, album ->
            onView(withId(R.id.freshAlbumList))
                .perform(scrollToPosition(index))
                .check(matches(atPositionOnView(index, R.id.albumName, withText(album.name))))
                .check(
                    matches(
                        atPositionOnView(
                            index,
                            R.id.artistName,
                            withText(album.artist!!.name)
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

        onView(withId(R.id.freshAlbumList))
            .perform(scrollToPosition(0))
            .perform(atPositionOnView(0, click()))

        onView(withId(R.id.details)).check(matches(isDisplayed()))

    }

    companion object {

        val artists = mapOf(
            quickArtist("Superband"),
            quickArtist("Megadude and the Rockers"),
            quickArtist("Person with a Guitar"),
            quickArtist("The Bad Breaths"),
            quickArtist("The Why")
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
            ),
            quickAlbum(
                id = 3,
                artist = artists["Person with a Guitar"]!!,
                name = "I put Nylon Strings on my Stratocaster",
                releaseDate = 1633020113580
            ),
            quickAlbum(
                id = 4,
                artist = artists["The Bad Breaths"]!!,
                name = "Banned in the Dentist's Office",
                releaseDate = 1633018913580
            ),
            quickAlbum(
                id = 5,
                artist = artists["The Why"]!!,
                name = "Johnny",
                releaseDate = 1633008313580
            )
        )
    }
}