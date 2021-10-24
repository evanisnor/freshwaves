package com.evanisnor.freshwaves.debugmenu.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.databinding.DebugMenuItemUpdaterInformationBinding
import com.evanisnor.freshwaves.debugmenu.BindingViewHolder
import com.evanisnor.freshwaves.debugmenu.DebugMenuData
import com.evanisnor.freshwaves.features.updater.UpdaterState

data class UpdaterInformation(
    val state: UpdaterState,
    val onUpdateNow: () -> Unit
) : DebugMenuData

class UpdaterInformationViewHolder(
    private val binding: DebugMenuItemUpdaterInformationBinding
) :
    RecyclerView.ViewHolder(binding.root),
    BindingViewHolder<UpdaterInformation> {

    companion object {
        fun create(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DebugMenuItemUpdaterInformationBinding.inflate(inflater, parent, false)
            return UpdaterInformationViewHolder(binding)
        }
    }

    override fun bind(debugMenuData: UpdaterInformation) {
        binding.apply {
            status.text = debugMenuData.state.name

            runUpdaterNowButton.apply {
                isEnabled = debugMenuData.state == UpdaterState.Idle
                setOnClickListener {
                    debugMenuData.onUpdateNow()
                }
            }
        }
    }
}
