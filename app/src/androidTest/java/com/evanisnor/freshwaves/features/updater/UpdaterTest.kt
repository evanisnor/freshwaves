package com.evanisnor.freshwaves.features.updater

import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
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
        hiltRule.inject()
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