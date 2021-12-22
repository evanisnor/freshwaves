package com.evanisnor.freshwaves.features.updater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class UpdaterBootstrapper @Inject constructor() {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                updateNow(context)
            }
        }
    }

    fun registerForSuccessfulAuthorization(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(
                broadcastReceiver,
                IntentFilter(SpotifyAuthorization.authorizationSuccessfulAction)
            )
    }

    fun updateNow(context: Context) {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<UpdateWorker>().build())
    }

    fun scheduleNextUpdate(context: Context): Instant {
        val targetStartTime = ZonedDateTime.now(ZoneId.systemDefault())
            .plusHours(1)
            .plusMinutes(1)

        val delay = Duration.between(
            ZonedDateTime.now(ZoneId.systemDefault()),
            targetStartTime
        )

        WorkManager.getInstance(context)
            .enqueue(
                OneTimeWorkRequestBuilder<UpdateWorker>()
                    .setInitialDelay(delay)
                    .build()
            )

        return targetStartTime.toInstant()
    }

}