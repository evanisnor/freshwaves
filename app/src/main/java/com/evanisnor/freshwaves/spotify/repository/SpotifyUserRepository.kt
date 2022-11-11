package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.user.UserProfile
import javax.inject.Inject

class SpotifyUserRepository @Inject constructor(
  private val spotifyNetworkRepository: SpotifyNetworkRepository,
) {

  suspend fun userProfile(): UserProfile = spotifyNetworkRepository.userProfile()

}