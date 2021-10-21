package com.evanisnor.freshwaves.spotify.network.model

import android.net.Uri
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalUrls(
    val spotify: String
)