package com.evanisnor.freshwaves.features.freshalbums

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.tools.TestDataLoader
import com.evanisnor.freshwaves.tools.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FreshAlbumsTest {

    private val freshAlbumsRobot = FreshAlbumsRobot()
    private val albumDetailsRobot = AlbumDetailsRobot()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var spotifyCacheDao: SpotifyCacheDao

    @Before
    fun setup() {
        hiltRule.inject()

        val context = ApplicationProvider.getApplicationContext<Context>()
        with(TestDataLoader(context)) {
            loadEntitiesRelationally(
                onArtists = { spotifyCacheDao.insertArtists(it) },
                onAlbums = { spotifyCacheDao.insertAlbums(it) },
                onTracks = { spotifyCacheDao.insertTracks(it) }
            )
        }
    }

    @Test
    fun freshAlbumsAreDisplayed() {
        lateinit var albumList: List<Album>
        runBlocking {
            albumList = spotifyCacheDao.readAlbumsWithImages(30).first()
        }

        val latch = CountDownLatch(1)
        launchFragmentInHiltContainer<FreshAlbumsFragment> {
            latch.countDown()
        }

        latch.await()
        albumList.forEachIndexed { index, album ->
            freshAlbumsRobot.verifyAlbumWithImage(index + 1, album)
        }
    }

    @Test
    fun clickFreshAlbumLaunchesAlbumDetails() {
        lateinit var albumList: List<Album>
        runBlocking {
            albumList = spotifyCacheDao.readAlbumsWithImages(30).first()
        }

        val latch = CountDownLatch(1)
        launchFragmentInHiltContainer<FreshAlbumsFragment> {
            latch.countDown()
        }

        latch.await()
        albumList.forEachIndexed { index, album ->
            freshAlbumsRobot.selectAlbumAt(index + 1)
            albumDetailsRobot.verifyAlbumOverview(album)
            pressBack()
        }
    }
}
