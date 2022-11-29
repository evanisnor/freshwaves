package com.evanisnor.freshwaves.debugmenu.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.databinding.DebugMenuItemUpdaterInformationBinding
import com.evanisnor.freshwaves.debugmenu.BindingViewHolder
import com.evanisnor.freshwaves.debugmenu.DebugMenuData
import com.evanisnor.freshwaves.features.updater.UpdaterState
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

data class UpdaterInformation(
  val state: UpdaterState,
  val lastRunState: UpdaterState,
  val lastRunOn: Instant?,
  val nextRunOn: Instant?,
  val onUpdateNow: () -> Unit,
  val onTestNotification: () -> Unit,
) : DebugMenuData

class UpdaterInformationViewHolder(
  private val binding: DebugMenuItemUpdaterInformationBinding,
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
      lastStatus.text = debugMenuData.lastRunState.name
      debugMenuData.lastRunOn?.let {
        lastRunOn.text = it
          .atOffset(currentOffset())
          .format(
            DateTimeFormatter.ofPattern("MMM d, y @ h:mm a", Locale.getDefault()),
          )
      }
      debugMenuData.nextRunOn?.let {
        nextRunOn.text = it
          .atOffset(currentOffset())
          .format(
            DateTimeFormatter.ofPattern("MMM d, y @ h:mm a", Locale.getDefault()),
          )
      }

      testNotificationButton.setOnClickListener {
        debugMenuData.onTestNotification()
      }

      runUpdaterNowButton.apply {
        isEnabled = debugMenuData.state == UpdaterState.Idle
        setOnClickListener {
          debugMenuData.onUpdateNow()
        }
      }
    }
  }

  private fun currentOffset(): ZoneOffset =
    ZoneOffset.of(ZoneId.systemDefault().rules.getOffset(Instant.now()).toString())
}
