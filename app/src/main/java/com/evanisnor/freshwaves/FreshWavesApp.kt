package com.evanisnor.freshwaves

import android.app.Application
import androidx.annotation.Keep
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.evanisnor.freshwaves.ads.AdIntegration
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.features.updater.UpdaterBootstrapper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class FreshWavesApp : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  @Inject
  lateinit var updaterBootstrapper: UpdaterBootstrapper

  @Inject
  lateinit var freshAlbumNotifier: FreshAlbumNotifier

  @Inject
  @Suppress("unused")
  @Keep
  lateinit var adIntegration: AdIntegration

  @Inject
  lateinit var trees: Set<@JvmSuppressWildcards Timber.Tree>

  override fun onCreate() {
    super.onCreate()
    Timber.plant(*trees.toTypedArray())
    updaterBootstrapper.registerForSuccessfulAuthorization()
    freshAlbumNotifier.createNotificationChannel()
  }

  override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
    .setWorkerFactory(workerFactory)
    .build()
}
