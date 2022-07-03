package com.evanisnor.freshwaves.features.attribution

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AttributionDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun readAttributionListJson(): String = readAssetFileText(attributionFileName)

    fun readLicenseText(fileName: String): String = readAssetFileText("$attributionDir/$fileName")

    private fun readAssetFileText(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }

    companion object {
        private const val attributionDir = "attribution"
        private const val attributionFileName = "$attributionDir/attribution.json"
    }

}