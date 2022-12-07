package com.evanisnor.freshwaves.features.freshalbums

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.evanisnor.freshwaves.LoginActivity
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.FreshAlbumsFragmentBinding
import com.evanisnor.freshwaves.features.albumdetails.AlbumDetailsFragment
import com.evanisnor.freshwaves.features.attribution.ThirdPartyUsageListFragment
import com.evanisnor.freshwaves.features.freshalbums.adapter.FreshAlbumsAdapter
import com.evanisnor.freshwaves.features.freshalbums.adapter.ObservableLinearLayoutManager
import com.evanisnor.freshwaves.features.updater.UpdaterState
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.system.DebugMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@AndroidEntryPoint
class FreshAlbumsFragment : Fragment() {

  @Inject
  lateinit var debugMenu: DebugMenu

  @Inject
  lateinit var spotifyAuthorization: SpotifyAuthorization

  @Inject
  lateinit var freshAlbumsAdapter: FreshAlbumsAdapter

  private val freshAlbumsViewModel: FreshAlbumsViewModel by activityViewModels()
  private var binding: FreshAlbumsFragmentBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return FreshAlbumsFragmentBinding.inflate(inflater, container, false)
      .apply {
        binding = this
      }.root
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val observableLinearLayoutManager = ObservableLinearLayoutManager(requireContext())

    binding?.apply {
      freshAlbumsList.apply {
        adapter = ConcatAdapter(HeaderAdapter(), freshAlbumsAdapter)
        layoutManager = observableLinearLayoutManager
      }

      toolbar.setOnMenuItemClickListener { item ->
        return@setOnMenuItemClickListener when (item.itemId) {
          R.id.logout -> {
            lifecycleScope.launch {
              spotifyAuthorization.logout()
              activity?.finish()
              startActivity(
                Intent(activity, LoginActivity::class.java),
              )
            }
            true
          }
          R.id.attribution_menu_item -> {
            activity?.apply {
              supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ThirdPartyUsageListFragment())
                .addToBackStack(ThirdPartyUsageListFragment.TAG)
                .commit()
            }
            true
          }
          else -> debugMenu.onMenuItemClick(activity, item.itemId)
        }
      }
    }

    registerAdapterClickListener()

    with(lifecycleScope) {
      launchWhenCreated {
        listenForFreshAlbums(observableLinearLayoutManager)
      }
      launchWhenResumed {
        listenForUpdaterStatus()
      }
    }
  }

  private fun toggleViews(state: UpdaterState, albumCount: Int) {
    if (state == UpdaterState.Idle || state == UpdaterState.Retry || state == UpdaterState.Unknown) return
    binding?.apply {
      loadingMessage.loading.isVisible = state == UpdaterState.Running
      freshAlbumsList.isVisible = state == UpdaterState.Success && albumCount > 0
      emptyMessage.empty.isVisible = state == UpdaterState.Success && albumCount == 0
      errorMessage.error.isVisible = state == UpdaterState.Failure
    }
  }

  private suspend fun listenForFreshAlbums(observableLinearLayoutManager: ObservableLinearLayoutManager) {
    freshAlbumsViewModel.albums.collect { albums ->
      freshAlbumsAdapter.submitList(albums)
      toggleViews(freshAlbumsViewModel.lastKnownUpdaterState(), albums.size)
      insertAdvertisements(observableLinearLayoutManager)
    }
  }

  private suspend fun listenForUpdaterStatus() {
    freshAlbumsViewModel.updaterState.collect { result ->
      toggleViews(result, freshAlbumsAdapter.itemCount)
    }
  }

  private fun registerAdapterClickListener() {
    freshAlbumsAdapter.listener = object : FreshAlbumsAdapter.OnAlbumSelectedListener {
      override fun onAlbumSelected(album: Album) {
        launchAlbumDetails(album)
      }
    }
  }

  private fun launchAlbumDetails(album: Album) {
    val albumDetailsFragment = AlbumDetailsFragment.create(album.id)

    activity?.supportFragmentManager?.apply {
      beginTransaction()
        .add(android.R.id.content, albumDetailsFragment)
        .addToBackStack(AlbumDetailsFragment.TAG)
        .commit()
    }
  }

  private fun insertAdvertisements(layoutManager: ObservableLinearLayoutManager) {
    layoutManager.whenLayoutCompleted {
      val lastVisiblePosition = max(findLastVisibleItemPosition(), 5)
      if (lastVisiblePosition > 0 && freshAlbumsAdapter.itemCount > 0) {
        val adIndexes = freshAlbumsAdapter.indexesThatNeedAds(lastVisiblePosition, 2)
        lifecycleScope.launch {
          val albumCardAds = freshAlbumsViewModel.generateAlbumCardAds(numberOfAds = adIndexes.size)
          freshAlbumsAdapter.insertAdvertisements(albumCardAds, adIndexes)
        }
        whenLayoutCompleted { }
      }
    }
  }
}
