package com.evanisnor.freshwaves.features.updater

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateWorker @AssistedInject constructor(
  @Assisted applicationContext: Context,
  @Assisted workerParameters: WorkerParameters,
  private val updaterController: UpdaterController,
) : CoroutineWorker(applicationContext, workerParameters) {

  override suspend fun doWork(): Result = updaterController.runUpdate()
}
