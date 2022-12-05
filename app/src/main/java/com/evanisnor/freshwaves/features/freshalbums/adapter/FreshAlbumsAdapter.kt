package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album

class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
  override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem

  override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
}

class FreshAlbumsAdapter : ListAdapter<Album, FreshAlbumCard>(AlbumDiffCallback()) {

  interface OnAlbumSelectedListener {
    fun onAlbumSelected(album: Album)
  }

  var listener: OnAlbumSelectedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreshAlbumCard =
    FreshAlbumCard.create(parent, listener)

  override fun onBindViewHolder(holder: FreshAlbumCard, position: Int) {
    holder.bind(getItem(position))
  }
}
