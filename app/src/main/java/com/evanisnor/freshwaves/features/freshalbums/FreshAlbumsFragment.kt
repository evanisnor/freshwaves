package com.evanisnor.freshwaves.features.freshalbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanisnor.freshwaves.FreshWavesApp
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.FreshAlbumsFragmentBinding
import com.evanisnor.freshwaves.features.albumdetails.AlbumDetailsFragment
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album

class FreshAlbumsFragment : Fragment() {

    private var fragmentFreshAlbumsBinding: FreshAlbumsFragmentBinding? = null
    private var freshAlbumsViewModel: FreshAlbumsViewModel? = null
    private val freshAlbumsAdapter = FreshAlbumsAdapter()

    private var _binding: FreshAlbumsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FreshAlbumsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            freshAlbumList.adapter = freshAlbumsAdapter
            freshAlbumList.layoutManager = LinearLayoutManager(context)
            fragmentFreshAlbumsBinding = this
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        freshAlbumsViewModel = ViewModelProvider(
            this, FreshAlbumsViewModelFactory(
                (context?.applicationContext as FreshWavesApp).spotifyAlbumRepository
            )
        ).get(FreshAlbumsViewModel::class.java)

        freshAlbumsViewModel?.getLatestAlbums { albums ->
            activity?.runOnUiThread {
                freshAlbumsAdapter.submitList(albums)
            }
        }

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
            ?.add(R.id.freshAlbumsContainer, albumDetailsFragment)
            ?.addToBackStack(AlbumDetailsFragment.TAG)
            ?.commit()
    }

}