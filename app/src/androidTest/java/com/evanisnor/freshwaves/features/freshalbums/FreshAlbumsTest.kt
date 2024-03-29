package com.evanisnor.freshwaves.features.freshalbums

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.tools.TestDataLoader
import com.evanisnor.freshwaves.tools.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
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
        onTracks = { spotifyCacheDao.insertTracks(it) },
      )
    }
  }

  @Test
  fun freshAlbumsAreDisplayed() = runTest {
    val albumList = spotifyCacheDao.readAlbumsWithImages(30).first()

    launchFragmentInHiltContainer<FreshAlbumsFragment>()

    albumList.forEachIndexed { index, album ->
      freshAlbumsRobot.verifyAlbumWithImage(index + 1, album)
    }
  }

  @Test
  fun clickFreshAlbumLaunchesAlbumDetails() = runTest {
    val albumList = spotifyCacheDao.readAlbumsWithImages(30).first()

    launchFragmentInHiltContainer<FreshAlbumsFragment>()
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()

    albumList.forEachIndexed { index, album ->
      freshAlbumsRobot.selectAlbumAt(index + 1)
      albumDetailsRobot.verifyAlbumOverview(album)
      pressBack()
    }
  }
}
