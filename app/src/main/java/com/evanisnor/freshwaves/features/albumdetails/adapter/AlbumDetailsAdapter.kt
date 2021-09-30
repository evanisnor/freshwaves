package com.evanisnor.freshwaves.features.albumdetails.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track

class AlbumDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class Type {
        Overview,
        Track,
        Disc
    }

    private var albumDetailsList = mutableListOf<Any>()

    init {
        setHasStableIds(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submit(album: Album) {
        albumDetailsList.clear()
        albumDetailsList.add(album)
        if (album.tracks.distinctBy {
                it.discNumber


            }.size > 1) {
            var disc = 0
            album.tracks.forEach { track ->
                if (track.discNumber != disc) {
                    disc = track.discNumber
                    albumDetailsList.add(Disc(disc))
                }
                albumDetailsList.add(track)
            }
        } else {
            album.tracks.forEach { albumDetailsList.add(it) }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = albumDetailsList.size

    override fun getItemId(position: Int): Long =
        when (val item = albumDetailsList[position]) {
            is Track -> (item.discNumber * 100) + item.trackNumber.toLong()
            is Disc -> item.number * 100L
            else -> 0
        }

    override fun getItemViewType(position: Int): Int = when (albumDetailsList[position]) {
        is Album -> Type.Overview.ordinal
        is Track -> Type.Track.ordinal
        is Disc -> Type.Disc.ordinal
        else -> -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            Type.Overview.ordinal -> AlbumOverview.create(parent)
            Type.Track.ordinal -> AlbumTrack.create(parent)
            Type.Disc.ordinal -> DiscHeader.create(parent)
            else -> object : RecyclerView.ViewHolder(parent) {}
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = albumDetailsList[position]
        when (holder.itemViewType) {
            Type.Overview.ordinal -> (holder as AlbumOverview).bind(item as Album)
            Type.Track.ordinal -> (holder as AlbumTrack).bind(item as Track)
            Type.Disc.ordinal -> (holder as DiscHeader).bind(item as Disc)
        }
    }
}