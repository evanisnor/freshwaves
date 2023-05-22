package com.evanisnor.freshwaves.features.permissionscheck

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.system.hasPermission
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber
import javax.inject.Inject

private val IS_PERMISSION_REQUIRED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS

class NotificationPermissionChecker @Inject constructor() {

  fun promptForNotificationPermission(activity: ComponentActivity) {
    if (IS_PERMISSION_REQUIRED && !activity.hasPermission(NOTIFICATION_PERMISSION)) {
      val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
          if (isGranted) {
            Timber.i("Notification permissions granted!")
          } else {
            Timber.i("Notification permissions were DENIED")
          }
        }

      showRationaleDialog(activity) { isRationaleAccepted ->
        if (isRationaleAccepted) {
          launcher.launch(NOTIFICATION_PERMISSION)
        } else {
          Timber.i("Notification rationale dialog was dismissed")
        }
      }
    }
  }

  private fun showRationaleDialog(context: Context, onClick: (Boolean) -> Unit) {
    MaterialAlertDialogBuilder(context)
      .setTitle(context.getString(R.string.notification_permission_rationale))
      .setNegativeButton(context.getString(R.string.notification_permission_decline)) { _, _ ->
        onClick(false)
      }
      .setPositiveButton(context.getString(R.string.notification_permission_accept)) { _, _ ->
        onClick(true)
      }
      .show()
  }
}
