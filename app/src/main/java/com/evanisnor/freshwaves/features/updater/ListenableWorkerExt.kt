package com.evanisnor.freshwaves.features.updater

import androidx.work.ListenableWorker

fun ListenableWorker.Result.toStatus() = when (this) {
  is ListenableWorker.Result.Success -> UpdaterState.Success
  is ListenableWorker.Result.Failure -> UpdaterState.Failure
  is ListenableWorker.Result.Retry -> UpdaterState.Retry
  else -> UpdaterState.Unknown
}
