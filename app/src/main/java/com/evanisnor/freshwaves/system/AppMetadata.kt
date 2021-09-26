package com.evanisnor.freshwaves.system

import android.content.Context
import android.content.pm.PackageManager

class AppMetadata {

    fun spotifyClientId(context: Context) = metaData(context, "spotifyClientId")

    fun spotifyRedirectUri(context: Context) = metaData(context, "spotifyRedirectUri")

    private fun metaData(context: Context, id: String) = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    ).metaData[id].toString()
}