package com.evanisnor.freshwaves

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.evanisnor.freshwaves.databinding.ConnectAccountFragmentBinding
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConnectAccountFragment : Fragment() {

  @Inject
  lateinit var spotifyAuthorization: SpotifyAuthorization

  private var binding: ConnectAccountFragmentBinding? = null
  private lateinit var pendingAuthorization: SpotifyAuthorization.PendingAuthorization

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View = ConnectAccountFragmentBinding.inflate(inflater, container, false)
    .apply {
      binding = this
    }.root

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launchWhenCreated {
      pendingAuthorization = spotifyAuthorization.prepareAuthorization(this@ConnectAccountFragment)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding?.connectSpotifyButton?.root?.setOnClickListener {
      lifecycleScope.launch {
        when (pendingAuthorization.authorize(requireContext())) {
          is SpotifyAuthorization.Response.Success -> proceedToFreshAlbums()
          is SpotifyAuthorization.Response.Failure -> {
            Snackbar.make(binding!!.root, "Login to Spotify failed", Snackbar.LENGTH_INDEFINITE)
              .show()
          }
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }

  private fun proceedToFreshAlbums() {
    startActivity(
      Intent(requireContext(), MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      },
    )
  }
}
