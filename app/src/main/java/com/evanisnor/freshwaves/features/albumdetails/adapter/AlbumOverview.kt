package com.evanisnor.freshwaves.features.albumdetails.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.AlbumDetailsOverviewItemBinding
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class AlbumOverview(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            AlbumOverview(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.album_details_overview_item, parent, false)
            )
    }

    fun bind(album: Album) {
        AlbumDetailsOverviewItemBinding.bind(itemView).apply {
            album.images.firstOrNull()?.let {
                albumImage?.load(it.url)
                albumImage?.tag = it.url
            }

            albumName.text = album.name
            artistName.text = album.artist?.name ?: "Unknown"

            releaseDate.text = album.releaseDate
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(
                    DateTimeFormatter.ofPattern("YYYY", Locale.getDefault())
                )

            playWithSpotifyButton?.setOnClickListener {
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = album.spotifyUri
                })
            }
        }
    }
}