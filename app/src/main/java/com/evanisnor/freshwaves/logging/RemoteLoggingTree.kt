package com.evanisnor.freshwaves.logging

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

/**
 * Remote logging for production
 */
class RemoteLoggingTree : Timber.Tree() {

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    when (priority) {
      Log.ERROR -> Firebase.crashlytics.log("ERROR: $tag$message")
      Log.WARN -> Firebase.crashlytics.log("WARN: $tag$message")
      Log.INFO -> Firebase.crashlytics.log("INFO: $tag$message")
    }

    if (t != null) {
      Firebase.crashlytics.recordException(t)
    }
  }
}
