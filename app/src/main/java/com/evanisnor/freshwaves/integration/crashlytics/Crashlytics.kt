package com.evanisnor.freshwaves.integration.crashlytics

import androidx.annotation.VisibleForTesting
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Vendor interface for Crashlytics
 */
interface Crashlytics {

  /**
   * Delegate for [FirebaseCrashlytics.setCustomKey]
   */
  fun setCustomKey(key: String, value: Long)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CrashlyticsModule {

  @Singleton
  @Binds
  abstract fun bindCrashlytics(impl: RealCrashlytics): Crashlytics
}

/**
 * Real delegate for [FirebaseCrashlytics]
 */
class RealCrashlytics @Inject constructor() : Crashlytics {

  /**
   * [Crashlytics.setCustomKey] -> [FirebaseCrashlytics.setCustomKey]
   */
  override fun setCustomKey(key: String, value: Long) {
    FirebaseCrashlytics.getInstance().setCustomKey(key, value)
  }
}

/**
 * Fake implementation of [Crashlytics] for use in tests.
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class FakeCrashlytics : Crashlytics {
  override fun setCustomKey(key: String, value: Long) {}
}
