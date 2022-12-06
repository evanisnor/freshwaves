package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.evanisnor.freshwaves.ads.AdIntegration
import com.evanisnor.freshwaves.ads.Advertisement
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import timber.log.Timber
import javax.inject.Inject

sealed interface AlbumListItem {
  class FreshAlbum(val album: Album) : AlbumListItem
  class AdCard(val ad: Advertisement) : AlbumListItem
}

class AlbumListDiffCallback : DiffUtil.ItemCallback<AlbumListItem>() {
  override fun areItemsTheSame(oldItem: AlbumListItem, newItem: AlbumListItem) = oldItem == newItem
  override fun areContentsTheSame(oldItem: AlbumListItem, newItem: AlbumListItem) =
    oldItem == newItem
}

class FreshAlbumsAdapter @Inject constructor(
  private val adIntegration: AdIntegration,
  private val adFreshAlbumCardFactory: AdFreshAlbumCardFactory,
) : RecyclerView.Adapter<ViewHolder>() {

  interface OnAlbumSelectedListener {
    fun onAlbumSelected(album: Album)
  }

  private val differ = AsyncListDiffer(this, AlbumListDiffCallback())
  var listener: OnAlbumSelectedListener? = null

  fun submitList(albums: List<Album>) {
    differ.submitList(albums.map { AlbumListItem.FreshAlbum(it) })
  }

  fun insertAdvertisements(lastVisiblePosition: Int, offset: Int = 0) {
    if (lastVisiblePosition <= offset) return
    differ.submitList(
      differ.currentList.toMutableList().apply {
        removeAll { it is AlbumListItem.AdCard }
      },
    )
    val numberOfAds =
      differ.currentList.filterNot { it is AlbumListItem.AdCard }.size / lastVisiblePosition + 1
    Timber.d("Inserting $numberOfAds Album Card ads")

    var adPosition = lastVisiblePosition - offset
    for (i in 0 until numberOfAds) {
      adIntegration.buildAlbumCardAd { ad ->
        differ.submitList(
          differ.currentList.toMutableList().apply {
            Timber.d("Inserting Album Card ad at position $adPosition")
            add(adPosition, AlbumListItem.AdCard(ad))
            adPosition += lastVisiblePosition
          },
        )
      }
    }
  }

  override fun getItemCount(): Int = differ.currentList.size

  override fun getItemViewType(position: Int): Int = when (differ.currentList[position]) {
    is AlbumListItem.FreshAlbum -> AlbumListItem.FreshAlbum::class.hashCode()
    is AlbumListItem.AdCard -> AlbumListItem.AdCard::class.hashCode()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    when (viewType) {
      AlbumListItem.FreshAlbum::class.hashCode() -> FreshAlbumCard.create(parent, listener)
      AlbumListItem.AdCard::class.hashCode() -> adFreshAlbumCardFactory.create(parent)
      else -> throw IllegalStateException("Unknown view type $viewType")
    }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when (holder) {
      is FreshAlbumCard -> holder.bind((differ.currentList[position] as AlbumListItem.FreshAlbum).album)
      is AdFreshAlbumCard -> holder.bind((differ.currentList[position] as AlbumListItem.AdCard).ad)
    }
  }
}
