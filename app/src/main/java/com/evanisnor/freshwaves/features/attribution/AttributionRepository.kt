package com.evanisnor.freshwaves.features.attribution

import com.evanisnor.freshwaves.features.attribution.model.AttributionList
import com.evanisnor.freshwaves.features.attribution.model.AttributionListJsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject

class AttributionRepository @Inject constructor(
  private val attributionDataSource: AttributionDataSource,
  moshi: Moshi,
) {
  private val attributionListJsonAdapter: AttributionListJsonAdapter =
    AttributionListJsonAdapter(moshi)

  fun getAttributionList(): AttributionList {
    val attributionListJson = attributionDataSource.readAttributionListJson()
    return attributionListJsonAdapter.fromJson(attributionListJson) ?: AttributionList()
  }

  fun getLicenseText(source: String): String {
    return attributionDataSource.readLicenseText(source)
  }
}
