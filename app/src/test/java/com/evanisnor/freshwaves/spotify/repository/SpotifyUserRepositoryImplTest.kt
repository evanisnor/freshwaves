package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.FakeSpotifyAPIService
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.user.UserProfile
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SpotifyUserRepositoryImplTest {

  private lateinit var spotifyAPIService: FakeSpotifyAPIService
  private lateinit var spotifyUserRepository: SpotifyUserRepositoryImpl

  @Before
  fun setup() {
    spotifyAPIService = FakeSpotifyAPIService()
    val spotifyNetworkRepository = SpotifyNetworkRepository(
      SpotifyAuthorizationImpl(
        FakeHandyAuth().apply {
          authorize()
        },
      ),
      spotifyAPIService,
    )
    spotifyUserRepository = SpotifyUserRepositoryImpl(spotifyNetworkRepository)
  }

  @Test
  fun `userProfile - when authorized - returns user profile`() = runTest {
    spotifyAPIService.privateUser = PrivateUserObject(
      id = "user-004",
      email = "user004@email.com",
      displayName = "Test",
      country = "X",
    )

    val userProfile = spotifyUserRepository.userProfile()

    assertThat(userProfile).isEqualTo(
      UserProfile(
        id = "user-004",
        email = "user004@email.com",
        name = "Test",
        country = "X",
      ),
    )
  }
}
