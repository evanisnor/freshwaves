package com.evanisnor.freshwaves.features.attribution.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.features.attribution.model.AttributionList
import com.evanisnor.freshwaves.features.attribution.model.License

class ThirdPartyUsageListAdapter(
    private val attributionList: AttributionList,
    private val onLicenseSelectedListener: OnLicenseSelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ThirdPartyUsageViewHolder.create(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ThirdPartyUsageViewHolder).bind(
            attributionList.thirdPartyUsage[position],
            onLicenseSelectedListener::onSelected
        )
    }

    override fun getItemCount(): Int = attributionList.thirdPartyUsage.size
}

interface OnLicenseSelectedListener {
    fun onSelected(license: License)
}