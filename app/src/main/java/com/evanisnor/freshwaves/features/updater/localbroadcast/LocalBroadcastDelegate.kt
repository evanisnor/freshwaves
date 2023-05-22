package com.evanisnor.freshwaves.features.updater.localbroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalBroadcastDelegateModule {

  @Singleton
  @Binds
  abstract fun bindLocalBroadcastDelegate(impl: RealLocalBroadcastDelegate): LocalBroadcastDelegate
}

/**
 * Interface for de-coupling [LocalBroadcastManager]
 */
interface LocalBroadcastDelegate {
  fun register(action: String, receiver: () -> Unit)
}

/**
 * Real implementation of [LocalBroadcastDelegate] to bridge between domain logic and [LocalBroadcastManager]
 */
class RealLocalBroadcastDelegate @Inject constructor(
  private val localBroadcastManager: LocalBroadcastManager,
) : LocalBroadcastDelegate {

  override fun register(action: String, receiver: () -> Unit) {
    localBroadcastManager.registerReceiver(
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          receiver()
        }
      },
      IntentFilter(action),
    )
  }
}
