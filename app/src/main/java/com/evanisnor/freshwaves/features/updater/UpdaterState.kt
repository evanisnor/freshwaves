package com.evanisnor.freshwaves.features.updater

enum class UpdaterState {
  Idle,
  Running,
  Success,
  Failure,
  Retry,
  Unknown
}