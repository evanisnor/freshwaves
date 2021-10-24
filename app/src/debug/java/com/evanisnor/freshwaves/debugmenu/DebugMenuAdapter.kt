package com.evanisnor.freshwaves.debugmenu

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.debugmenu.items.AppInformation
import com.evanisnor.freshwaves.debugmenu.items.AppInformationViewHolder
import com.evanisnor.freshwaves.debugmenu.items.UpdaterInformation
import com.evanisnor.freshwaves.debugmenu.items.UpdaterInformationViewHolder
import kotlin.reflect.KClass

interface DebugMenuData {
    val id: Int
        get() = this::class.hashCode()
}

interface BindingViewHolder<D : DebugMenuData> {
    fun bind(debugMenuData: D)
}

class DebugMenuAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableMapOf<KClass<*>, DebugMenuData>()
    private val items: List<DebugMenuData>
        get() = data.values.toList()

    init {
        setHasStableIds(true)
    }

    fun submit(debugMenuData: DebugMenuData) {
        data[debugMenuData::class] = debugMenuData
        notifyItemChanged(items.indexOf(debugMenuData))
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].id

    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            AppInformation::class.hashCode() -> AppInformationViewHolder.create(parent)
            UpdaterInformation::class.hashCode() -> UpdaterInformationViewHolder.create(parent)
            else -> throw IllegalStateException("ViewHolder undefined for viewType $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        if (holder is AppInformationViewHolder && item is AppInformation) {
            holder.bind(item)
        } else if (holder is UpdaterInformationViewHolder && item is UpdaterInformation) {
            holder.bind(item)
        }
    }

}