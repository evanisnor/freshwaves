package com.evanisnor.freshwaves.features.updater

import androidx.work.ListenableWorker

fun ListenableWorker.Result.toStatus() = when (this) {
    is ListenableWorker.Result.Success -> UpdaterStatus.Success
    is ListenableWorker.Result.Failure -> UpdaterStatus.Failure
    is ListenableWorker.Result.Retry -> UpdaterStatus.Retry
    else -> UpdaterStatus.Unknown
}