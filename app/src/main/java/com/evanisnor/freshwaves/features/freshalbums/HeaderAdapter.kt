package com.evanisnor.freshwaves.features.freshalbums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.R

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  companion object {
    fun create(parent: ViewGroup) =
      HeaderViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.spacer, parent, false)
      )
  }

}

class HeaderAdapter : RecyclerView.Adapter<HeaderViewHolder>() {

  override fun getItemCount(): Int = 1

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder =
    HeaderViewHolder.create(parent)

  override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
  }
}