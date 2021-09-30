package com.evanisnor.freshwaves.features.albumdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.AlbumDetailsTrackItemBinding
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track

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
            track.duration.seconds.let { seconds ->
                duration.text = String.format("%02d:%02d", seconds / 60, (seconds % 60))
            }
        }
    }

}