package com.evanisnor.freshwaves.features.freshalbums

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.FreshAlbumsFragmentBinding
import com.evanisnor.freshwaves.debugmenu.DebugMenuActivity
import com.evanisnor.freshwaves.features.albumdetails.AlbumDetailsFragment
import com.evanisnor.freshwaves.features.updater.UpdaterState
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FreshAlbumsFragment : Fragment() {

    private val freshAlbumsViewModel: FreshAlbumsViewModel by activityViewModels()
    private val freshAlbumsAdapter = FreshAlbumsAdapter()
    private var binding: FreshAlbumsFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

        binding?.apply {
            freshAlbumsList.apply {
                adapter = ConcatAdapter(HeaderAdapter(), freshAlbumsAdapter)
                layoutManager = LinearLayoutManager(context)
            }

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.debug_menu_item -> startActivity(
                        Intent(activity, DebugMenuActivity::class.java)
                    )
                }

                false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAdapterClickListener()
        freshAlbumsViewModel.registerForUpdaterStatus(requireContext())

        lifecycleScope.launch {
            listenForFreshAlbums()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            listenForUpdaterStatus()
        }
    }

    private fun toggleLoadingMessage(showLoadingMessage: Boolean) {
        binding?.apply {
            if (showLoadingMessage) {
                emptyMessage.loading.visibility = View.VISIBLE
                freshAlbumsList.visibility = View.INVISIBLE
            } else {
                emptyMessage.loading.visibility = View.GONE
                freshAlbumsList.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun listenForUpdaterStatus() {
        freshAlbumsViewModel.updaterState.collect { result ->
            toggleLoadingMessage(result == UpdaterState.Running)
        }
    }

    private suspend fun listenForFreshAlbums() {
        freshAlbumsViewModel.albums.collect { albums ->
            freshAlbumsAdapter.submitList(albums)
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
        val albumDetailsFragment = AlbumDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(AlbumDetailsFragment.albumIdArgument, album.id)
            }
        }

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(android.R.id.content, albumDetailsFragment)
            ?.addToBackStack(AlbumDetailsFragment.TAG)
            ?.commit()
    }

}