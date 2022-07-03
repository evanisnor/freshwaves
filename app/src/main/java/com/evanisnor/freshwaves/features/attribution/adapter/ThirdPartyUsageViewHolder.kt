package com.evanisnor.freshwaves.features.attribution.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.ThirdPartyUsageCardBinding
import com.evanisnor.freshwaves.databinding.ThirdPartyUsageCardItemBinding
import com.evanisnor.freshwaves.features.attribution.model.License
import com.evanisnor.freshwaves.features.attribution.model.ThirdPartySource
import com.evanisnor.freshwaves.features.attribution.model.ThirdPartyUsage

class ThirdPartyUsageViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) = ThirdPartyUsageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.third_party_usage_card, parent, false)
        )
    }

    fun bind(
        thirdPartyUsage: ThirdPartyUsage,
        onLicenseButtonSelected: (License) -> Unit = {}
    ) {

        ThirdPartyUsageCardBinding.bind(itemView).apply {
            thirdPartyUsageName.text = thirdPartyUsage.name
            thirdPartyUsageAuthor.text = thirdPartyUsage.author
            thirdPartyUsageLicenceButton.apply {
                text = thirdPartyUsage.license.name
                setOnClickListener {
                    onLicenseButtonSelected(thirdPartyUsage.license)
                }
            }

            thirdPartyUsageModifications.apply {
                isVisible = thirdPartyUsage.modifications.isNotEmpty()
                text = context.resources.getString(
                    R.string.attribution_modifications,
                    thirdPartyUsage.modifications.joinToString(",") { it.description }
                )
            }

            thirdPartyUsageInformation.removeAllViews()
            thirdPartyUsage.sources.forEach { source ->
                createInformationLineItem(thirdPartyUsageInformation, source)
            }
        }
    }

    private fun createInformationLineItem(
        parent: ViewGroup,
        source: ThirdPartySource
    ): ThirdPartyUsageCardItemBinding {
        val inflater: LayoutInflater = LayoutInflater.from(itemView.context)
        return ThirdPartyUsageCardItemBinding.inflate(inflater, parent, true)
            .apply {
                usageSourceLabel.text = source.artifactType.name
                usageSource.text = source.name
                usageSource.setOnClickListener {
                    itemView.context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(source.url)
                    })
                }
            }
    }
}