package com.evanisnor.freshwaves.features.attribution.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttributionList(
  val thirdPartyUsage: List<ThirdPartyUsage> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class ThirdPartyUsage(
  val name: String,
  val author: String,
  val license: License,
  val usage: String,
  val sources: List<ThirdPartySource>,
  val modifications: List<Modification> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class License(
  val name: String,
  val refType: LicenseReferenceType,
  val source: String,
)

@JsonClass(generateAdapter = true)
data class ThirdPartySource(
  val artifactType: ArtifactType,
  val name: String,
  val url: String,
)

@JsonClass(generateAdapter = true)
data class Modification(
  val description: String,
)

@Keep
enum class LicenseReferenceType {
  URL,
  EMBEDDED,
}

@Keep
enum class ArtifactType {
  API,
  CODE,
  BINARY,
  SVG,
}
