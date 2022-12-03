package com.evanisnor.freshwaves.user

import app.cash.turbine.test
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.ext.launchInFragment
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class UserStateRepositoryTest {

  @Test
  fun `currentState - when no user logged in - emits NoUser`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(FakeHandyAuth())
    val userStateRepository = UserStateRepository(spotifyAuthorization)

    userStateRepository.currentState.test {
      assertThat(awaitItem()).isEqualTo(UserStateRepository.State.NoUser)
    }
  }

  @Test
  fun `currentState - when user becomes authorized - emits LoggedIn`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(FakeHandyAuth())
    val userStateRepository = UserStateRepository(spotifyAuthorization)

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment).authorize()

      userStateRepository.currentState.test {
        assertThat(awaitItem()).isEqualTo(UserStateRepository.State.LoggedIn)
      }
    }
  }

  @Test
  fun `currentState - when user logs out - emits NoUser`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        authorize()
      },
    )
    val userStateRepository = UserStateRepository(spotifyAuthorization)

    launchInFragment {
      spotifyAuthorization.logout()

      userStateRepository.currentState.test {
        assertThat(awaitItem()).isEqualTo(UserStateRepository.State.NoUser)
      }
    }
  }

  @Test
  fun `currentState - when user is logged in - emits LoggedIn`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        authorize()
      },
    )
    val userStateRepository = UserStateRepository(spotifyAuthorization)

    userStateRepository.currentState.test {
      assertThat(awaitItem()).isEqualTo(UserStateRepository.State.LoggedIn)
    }
  }
}
