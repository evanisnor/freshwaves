package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.FreshAlbumCardBinding
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class FreshAlbumCard(
  itemView: View,
  private val listener: FreshAlbumsAdapter.OnAlbumSelectedListener?,
) :
  RecyclerView.ViewHolder(itemView) {

  companion object {
    fun create(parent: ViewGroup, listener: FreshAlbumsAdapter.OnAlbumSelectedListener?) =
      FreshAlbumCard(
        LayoutInflater.from(parent.context)
          .inflate(R.layout.fresh_album_card, parent, false),
        listener,
      )
  }

  fun bind(album: Album) {
    FreshAlbumCardBinding.bind(itemView).apply {
      album.images.firstOrNull()?.let {
        albumImage.load(it.url)
        albumImage.tag = it.url
      }

      albumName.text = album.name
      artistName.text = album.artist?.name ?: "Unknown"

      releaseDate.text = album.releaseDate
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(
          DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault()),
        )
    }

    itemView.setOnClickListener {
      listener?.onAlbumSelected(album)
    }
  }
}
