package com.evanisnor.freshwaves.features.albumdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.evanisnor.freshwaves.databinding.AlbumDetailsFragmentBinding
import com.evanisnor.freshwaves.features.albumdetails.adapter.AlbumDetailsAdapter
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumDetailsFragment : Fragment() {

    companion object {
        const val TAG = "AlbumDetailsFragment"
        const val albumIdArgument =
            "com.evanisnor.freshwaves.features.albumdetails.arg.AlbumId"
    }

    private val albumDetailsViewModel: AlbumDetailsViewModel by activityViewModels()

    private var _binding: AlbumDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private var albumId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumId = arguments?.getInt(albumIdArgument)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlbumDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            details.apply {
                adapter = AlbumDetailsAdapter()
                layoutManager = LinearLayoutManager(context)
            }
        }

        albumId?.let { id ->
            lifecycleScope.launch {
                update(albumDetailsViewModel.getAlbumWithTracks(id))
            }
        }
    }

    private fun update(album: Album) {
        (binding.details.adapter as AlbumDetailsAdapter).submit(album)

        binding.albumImage?.apply {
            load(album.images.first().url)
        }
    }
}