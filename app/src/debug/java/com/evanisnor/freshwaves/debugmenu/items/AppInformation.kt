package com.evanisnor.freshwaves.debugmenu.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.databinding.DebugMenuItemAppInformationBinding
import com.evanisnor.freshwaves.debugmenu.BindingViewHolder
import com.evanisnor.freshwaves.debugmenu.DebugMenuData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


data class AppInformation(
    val version: String,
    val versionCode: Int,
    val builtOn: Instant
) : DebugMenuData

class AppInformationViewHolder(
    private val binding: DebugMenuItemAppInformationBinding
) :
    RecyclerView.ViewHolder(binding.root),
    BindingViewHolder<AppInformation> {

    companion object {
        fun create(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DebugMenuItemAppInformationBinding.inflate(inflater, parent, false)
            return AppInformationViewHolder(binding)
        }
    }

    override fun bind(debugMenuData: AppInformation) {
        binding.apply {
            version.text = debugMenuData.version
            versionCode.text = debugMenuData.versionCode.toString()
            buildDate.text = debugMenuData.builtOn
                .atZone(ZoneId.systemDefault())
                .format(
                    DateTimeFormatter.ofPattern("MMM d, y @ h:mm a", Locale.getDefault())
                )
        }
    }

}
