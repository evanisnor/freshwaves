package com.evanisnor.freshwaves.logging

import timber.log.Timber

/**
 * No-op logging for production
 */
class DeadTree : Timber.Tree() {
  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {}
}
