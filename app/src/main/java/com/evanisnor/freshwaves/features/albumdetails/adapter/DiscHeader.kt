package com.evanisnor.freshwaves.features.albumdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.AlbumDetailsDiscItemBinding

class DiscHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {

  companion object {
    fun create(parent: ViewGroup) =
      DiscHeader(
        LayoutInflater.from(parent.context)
          .inflate(R.layout.album_details_disc_item, parent, false),
      )
  }

  fun bind(disc: Disc) {
    AlbumDetailsDiscItemBinding.bind(itemView).apply {
      discLabel.text = "Disc ${disc.number}"
    }
  }
}
