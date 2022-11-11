package com.evanisnor.freshwaves.features.updater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager


fun LocalBroadcastManager.register(
  intentFilter: IntentFilter,
  receiver: () -> Unit,
) {
  registerReceiver(
    object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        receiver()
      }
    },
    intentFilter
  )
}