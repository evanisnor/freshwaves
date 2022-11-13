package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor


/**
 * A solution for espresso execution to perform diffs on MainThread
 * From: https://github.com/android/android-test/issues/382
 */
abstract class ThreadedListAdapter<T, VH : RecyclerView.ViewHolder>(
  itemCallback: DiffUtil.ItemCallback<T>,
) :
  ListAdapter<T, VH>(
    AsyncDifferConfig.Builder(itemCallback)
      .apply {
        if (isEspresso()) setBackgroundThreadExecutor(MainThreadExecutor())
      }
      .build()
  )

/**
 *  This is a fork of internal MainThreadExecutor from [AsyncListDiffer]
 */
private class MainThreadExecutor : Executor {
  val mHandler = Handler(Looper.getMainLooper())
  override fun execute(command: Runnable) {
    mHandler.post(command)
  }
}

private fun isEspresso(): Boolean {
  return try {
    Class.forName("androidx.test.espresso.Espresso")
    true
  } catch (e: ClassNotFoundException) {
    false
  }
}