package com.evanisnor.freshwaves.features.updater.workmanager

import androidx.work.WorkRequest

/**
 * Fake [WorkManagerDelegate] used for testing
 */
class FakeWorkManager(
  val queue: MutableList<WorkRequest> = mutableListOf(),
) : WorkManagerDelegate {
  override fun enqueue(workRequest: WorkRequest) {
    queue.add(workRequest)
  }
}
