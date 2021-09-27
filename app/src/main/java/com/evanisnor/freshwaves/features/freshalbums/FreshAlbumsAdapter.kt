package com.evanisnor.freshwaves.features.freshalbums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.FreshAlbumCardBinding
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class FreshAlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            FreshAlbumViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fresh_album_card, parent, false)
            )
    }

    fun bind(album: Album) {
        FreshAlbumCardBinding.bind(itemView).apply {

            album.images.firstOrNull()?.let {
                albumImage.load(it.url)
            }

            name.text = album.name
            artist.text = album.artist?.name ?: "Unknown"

            releaseDate.text = album.releaseDate
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(
                    DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())
                )
        }
    }

}

class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
}

class FreshAlbumsAdapter : ListAdapter<Album, FreshAlbumViewHolder>(AlbumDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreshAlbumViewHolder {
        return FreshAlbumViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FreshAlbumViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }
}