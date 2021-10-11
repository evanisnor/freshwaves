package com.evanisnor.freshwaves.features.updater

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.evanisnor.freshwaves.fakes.FakeSpotifyAPIService
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.model.*
import com.evanisnor.freshwaves.tools.TestData
import com.evanisnor.freshwaves.tools.TestDataLoader
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UpdaterTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var spotifyAPIService: SpotifyAPIService

    @Inject
    lateinit var spotifyCacheDao: SpotifyCacheDao

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        hiltRule.inject()

        (spotifyAPIService as FakeSpotifyAPIService).apply {

            with(TestDataLoader(context)) {
                loadData<PrivateUserObject>(TestData.User) {
                    user = it
                }

                loadData<PagingObject<ArtistObject>>(TestData.Artists) {
                    queueArtists(it)
                }

                loadData<PagingObject<AlbumObject>>(TestData.Albums) {
                    queueAlbums(it)
                }

                loadData<PagingObject<TrackObject>>(TestData.Tracks) {
                    queueTracks(it)
                }
            }
        }
    }

    @Test
    fun workerTest() {
        val updateWorker = createUpdateWorker()
        val result = updateWorker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    private fun createUpdateWorker() = TestListenableWorkerBuilder.from(
        ApplicationProvider.getApplicationContext(),
        UpdateWorker::class.java
    ).setWorkerFactory(workerFactory)
        .build()
}