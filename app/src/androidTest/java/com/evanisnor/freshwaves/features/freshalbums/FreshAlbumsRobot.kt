package com.evanisnor.freshwaves.features.freshalbums

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.tools.RecyclerViewUtils
import org.hamcrest.CoreMatchers
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class FreshAlbumsRobot {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())

    fun verifyAlbumWithImage(index: Int, expectedAlbum: Album) {
        onView(ViewMatchers.withId(R.id.freshAlbumList))
            .perform(RecyclerViewUtils.scrollToPosition(index))
            .check(matches(isDisplayed()))
            .check(albumNameMatches(index, expectedAlbum.name))
            .check(artistNameMatches(index, expectedAlbum.artist!!.name))
            .check(albumImageUrlMatches(index, expectedAlbum.images.first().url))
            .check(albumReleaseDateMatches(index, expectedAlbum.releaseDate))
    }

    fun selectAlbumAt(index: Int) {
        onView(ViewMatchers.withId(R.id.freshAlbumList))
            .perform(RecyclerViewUtils.scrollToPosition(index))
            .perform(RecyclerViewUtils.atPositionOnView(index, ViewActions.click()))
    }

    // region Private Matchers

    private fun albumNameMatches(index: Int, expectedAlbumName: String) = matches(
        RecyclerViewUtils.atPositionOnView(
            index, R.id.albumName, ViewMatchers.withText(expectedAlbumName)
        )
    )

    private fun artistNameMatches(index: Int, expectedArtistName: String) = matches(
        RecyclerViewUtils.atPositionOnView(
            index, R.id.artistName,
            ViewMatchers.withText(expectedArtistName)
        )
    )

    private fun albumImageUrlMatches(index: Int, expectedUrl: String) = matches(
        RecyclerViewUtils.atPositionOnView(
            index, R.id.albumImage,
            ViewMatchers.withTagValue(
                CoreMatchers.`is`(expectedUrl)
            )
        )
    )

    private fun albumReleaseDateMatches(index: Int, expectedReleaseDate: Instant) = matches(
        RecyclerViewUtils.atPositionOnView(
            index, R.id.releaseDate, ViewMatchers.withText(
                expectedReleaseDate.atZone(
                    ZoneId.systemDefault()
                )
                    .toLocalDate()
                    .format(dateTimeFormatter)
            )
        )
    )

    // region

}