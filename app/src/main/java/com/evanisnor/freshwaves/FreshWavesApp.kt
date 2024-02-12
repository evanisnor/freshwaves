package com.evanisnor.freshwaves

import android.app.Application
import androidx.annotation.Keep
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.evanisnor.freshwaves.ads.AdIntegration
import com.evanisnor.freshwaves.ext.wrapHttpException
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.features.updater.UpdaterBootstrapper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class FreshWavesApp : Application(), Configuration.Provider {
  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface HiltWorkerFactoryEntryPoint {
    fun workerFactory(): HiltWorkerFactory
  }

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

  override val workManagerConfiguration: Configuration =
    Configuration.Builder()
      .setWorkerFactory(EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory())
      .build()

  override fun onCreate() {
    super.onCreate()
    setupUncaughtExceptionHandling()
    Timber.plant(*trees.toTypedArray())
    freshAlbumNotifier.createNotificationChannel()

    CoroutineScope(Dispatchers.Default).launch {
      updaterBootstrapper.runUpdateAfterAuthorization()
    }
  }

  private fun setupUncaughtExceptionHandling() {
    val oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { t, throwable ->
      Firebase.crashlytics.recordException(throwable)
      oldExceptionHandler?.uncaughtException(t, throwable.wrapHttpException())
    }
  }
}
