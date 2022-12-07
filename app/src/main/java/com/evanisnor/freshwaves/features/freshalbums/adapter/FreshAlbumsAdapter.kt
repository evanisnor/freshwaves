package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.evanisnor.freshwaves.ads.Advertisement
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import timber.log.Timber
import javax.inject.Inject

sealed interface AlbumListItem {
  class FreshAlbumItem(val album: Album) : AlbumListItem
  class AdvertisementItem(val ad: Advertisement) : AlbumListItem
}

class AlbumListDiffCallback : DiffUtil.ItemCallback<AlbumListItem>() {
  override fun areItemsTheSame(oldItem: AlbumListItem, newItem: AlbumListItem) = oldItem == newItem
  override fun areContentsTheSame(oldItem: AlbumListItem, newItem: AlbumListItem) =
    oldItem == newItem
}

class FreshAlbumsAdapter @Inject constructor(
  private val adFreshAlbumCardFactory: AdFreshAlbumCardFactory,
) : RecyclerView.Adapter<ViewHolder>() {

  interface OnAlbumSelectedListener {
    fun onAlbumSelected(album: Album)
  }

  private val differ = AsyncListDiffer(this, AlbumListDiffCallback())
  var listener: OnAlbumSelectedListener? = null

  fun submitList(albums: List<Album>) {
    differ.submitList(albums.map { AlbumListItem.FreshAlbumItem(it) })
  }

  fun indexesThatNeedAds(
    lastVisiblePosition: Int,
    offset: Int = 0,
  ): List<Int> {
    if (lastVisiblePosition <= offset) return emptyList()
    val increment = lastVisiblePosition - offset
    var i = increment
    val adIndexes = mutableListOf<Int>()
    while (i < itemCount) {
      adIndexes.add(0, i)
      i += increment
    }
    return adIndexes
  }

  fun insertAdvertisements(
    albumCardAds: List<Advertisement>,
    adIndexes: List<Int>,
  ) {
    Timber.d("Inserting ${adIndexes.size} Album Card ads")
    val adIndexQueue = adIndexes.toMutableList()
    differ.submitList(
      differ.currentList.toMutableList().apply {
        for (albumCardAd in albumCardAds) {
          val adIndex = adIndexQueue.removeAt(0)
          Timber.d("Inserting Album Card ad at position $adIndex")
          add(adIndex, AlbumListItem.AdvertisementItem(albumCardAd))
        }
      },
    )
  }

  override fun getItemCount(): Int = differ.currentList.size

  override fun getItemViewType(position: Int): Int = when (differ.currentList[position]) {
    is AlbumListItem.FreshAlbumItem -> AlbumListItem.FreshAlbumItem::class.hashCode()
    is AlbumListItem.AdvertisementItem -> AlbumListItem.AdvertisementItem::class.hashCode()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    when (viewType) {
      AlbumListItem.FreshAlbumItem::class.hashCode() -> FreshAlbumCard.create(parent, listener)
      AlbumListItem.AdvertisementItem::class.hashCode() -> adFreshAlbumCardFactory.create(parent)
      else -> throw IllegalStateException("Unknown view type $viewType")
    }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when (holder) {
      is FreshAlbumCard -> holder.bind((differ.currentList[position] as AlbumListItem.FreshAlbumItem).album)
      is AdFreshAlbumCard -> holder.bind((differ.currentList[position] as AlbumListItem.AdvertisementItem).ad)
    }
  }
}
