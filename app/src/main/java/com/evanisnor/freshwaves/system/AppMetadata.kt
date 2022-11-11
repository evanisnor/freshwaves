package com.evanisnor.freshwaves.system

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import javax.inject.Inject

class AppMetadata @Inject constructor() {

  fun spotifyClientId(context: Context) = metaData(context, "spotifyClientId")

  fun spotifyRedirectUri(context: Context) = metaData(context, "spotifyRedirectUri")

  private fun metaData(context: Context, id: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
      )
    } else {
      @Suppress("DEPRECATION")
      context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
      )
    }.metaData.getString(id)
      ?: throw IllegalStateException("Unable to read app metadata")
}