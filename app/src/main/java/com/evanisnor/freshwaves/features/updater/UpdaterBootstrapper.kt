package com.evanisnor.freshwaves.features.updater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorization
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class UpdaterBootstrapper @Inject constructor() {

    companion object {
        const val updaterStatusIntent = "updater-status-intent"
        const val updaterStatusExtra = "updater-status-extra"
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                updateNow(context)
            }
        }
    }

    fun register(context: Context) {
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

    fun broadcastStatus(context: Context, status: UpdaterStatus) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(updaterStatusIntent).apply {
                putExtra(updaterStatusExtra, status)
            })
    }

    fun scheduleNextUpdate(context: Context) {
        val targetStartTime = LocalDateTime.now()
            .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
            .withHour(6)

        val delay = Duration.between(LocalDateTime.now(), targetStartTime)

        WorkManager.getInstance(context)
            .enqueue(
                OneTimeWorkRequestBuilder<UpdateWorker>()
                    .setInitialDelay(delay)
                    .build()
            )
    }

}