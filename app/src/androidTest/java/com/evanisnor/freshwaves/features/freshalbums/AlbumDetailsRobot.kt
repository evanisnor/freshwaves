package com.evanisnor.freshwaves.features.freshalbums

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.tools.RecyclerViewUtils
import org.hamcrest.CoreMatchers
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class AlbumDetailsRobot {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())

    fun verifyAlbumOverview(expectedAlbum: Album) {
        onView(withId(R.id.details))
            .perform(RecyclerViewUtils.scrollToPosition(0))
            .check(ViewAssertions.matches(isDisplayed()))
            .check(albumImageUrlMatches(expectedAlbum.images.first().url))
            .check(albumNameMatches(expectedAlbum.name))
            .check(artistNameMatches(expectedAlbum.artist!!.name))
            .check(albumReleaseDateMatches(expectedAlbum.releaseDate))
    }


    // region Private Matchers

    private fun albumNameMatches(expectedAlbumName: String) = ViewAssertions.matches(
        RecyclerViewUtils.atPositionOnView(
            0, R.id.albumName, ViewMatchers.withText(expectedAlbumName)
        )
    )

    private fun artistNameMatches(expectedArtistName: String) = ViewAssertions.matches(
        RecyclerViewUtils.atPositionOnView(
            0, R.id.artistName,
            ViewMatchers.withText(expectedArtistName)
        )
    )

    private fun albumImageUrlMatches(expectedUrl: String) = ViewAssertions.matches(
        RecyclerViewUtils.atPositionOnView(
            0, R.id.albumImage,
            ViewMatchers.withTagValue(
                CoreMatchers.`is`(expectedUrl)
            )
        )
    )

    private fun albumReleaseDateMatches(expectedReleaseDate: Instant) =
        ViewAssertions.matches(
            RecyclerViewUtils.atPositionOnView(
                0, R.id.releaseDate, ViewMatchers.withText(
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