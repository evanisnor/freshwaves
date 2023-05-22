package com.evanisnor.freshwaves.features.updater.workmanager

import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkManagerDelegateModule {

  @Singleton
  @Binds
  abstract fun bindWorkManagerDelegate(impl: RealWorkManagerDelegate): WorkManagerDelegate
}

/**
 * Interface for de-coupling app code from [WorkManager]
 */
interface WorkManagerDelegate {
  fun enqueue(workRequest: WorkRequest)
}

/**
 * Real implementation of [WorkManagerDelegate] for bridging to [WorkManager]
 */
class RealWorkManagerDelegate @Inject constructor(
  private val workManager: WorkManager,
) : WorkManagerDelegate {

  override fun enqueue(workRequest: WorkRequest) {
    workManager.enqueue(workRequest)
  }
}
