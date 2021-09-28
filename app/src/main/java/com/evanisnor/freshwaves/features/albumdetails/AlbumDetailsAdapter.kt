package com.evanisnor.freshwaves.features.albumdetails

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.AlbumDetailsOverviewItemBinding
import com.evanisnor.freshwaves.databinding.AlbumDetailsTrackItemBinding
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
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
            albumImage?.apply {
                load(album.images.first().url)
            }

            albumName.text = album.name
            artistName.text = album.artist?.name ?: "Unknown"

            releaseDate.text = album.releaseDate
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(
                    DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())
                )
        }
    }
}

class AlbumTrack(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            AlbumTrack(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.album_details_track_item, parent, false)
            )
    }

    fun bind(track: Track) {
        AlbumDetailsTrackItemBinding.bind(itemView).apply {
            trackNumber.text = "${track.trackNumber}."
            trackName.text = track.name
            duration.text = track.duration
        }
    }

}

class AlbumDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class Type {
        Overview,
        Track
    }

    private var albumDetailsList = mutableListOf<Any>()

    init {
        setHasStableIds(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submit(album: Album) {
        albumDetailsList.clear()
        albumDetailsList.add(album)
        album.tracks.forEach { albumDetailsList.add(it) }
        notifyDataSetChanged()
    }

    override fun getItemCount() = albumDetailsList.size

    override fun getItemViewType(position: Int): Int = when (albumDetailsList[position]) {
        is Album -> Type.Overview.ordinal
        is Track -> Type.Track.ordinal
        else -> -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            Type.Overview.ordinal -> AlbumOverview.create(parent)
            Type.Track.ordinal -> AlbumTrack.create(parent)
            else -> object : RecyclerView.ViewHolder(parent) {}
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = albumDetailsList[position]
        when (holder.itemViewType) {
            Type.Overview.ordinal -> (holder as AlbumOverview).bind(item as Album)
            Type.Track.ordinal -> (holder as AlbumTrack).bind(item as Track)
        }
    }
}