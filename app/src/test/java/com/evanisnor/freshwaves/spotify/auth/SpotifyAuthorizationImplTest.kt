package com.evanisnor.freshwaves.spotify.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.lifecycleScope
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.handyauth.client.HandyAuth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SpotifyAuthorizationImplTest {

  @Test
  fun `isAuthorized - when HandyAuth is authorized - returns true`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Authorized
      },
    )

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment)
        .authorize(fragment.requireContext())
    }

    assertThat(spotifyAuthorization.isAuthorized).isTrue()
  }

  @Test
  fun `isAuthorized - when HandyAuth is not authorized - returns false`() = runTest {
    val handyAuth = FakeHandyAuth().apply {
      expectedAuthResult = HandyAuth.Result.Error.Denied
    }
    val spotifyAuthorization = SpotifyAuthorizationImpl(handyAuth)

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment)
        .authorize(fragment.requireContext())
    }

    assertThat(spotifyAuthorization.isAuthorized).isFalse()
  }

  @Test
  fun `authorize - when authorization granted - returns Success`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Authorized
      },
    )

    launchInFragment { fragment ->
      val response = spotifyAuthorization.prepareAuthorization(fragment)
        .authorize(fragment.requireContext())

      assertThat(response).isEqualTo(SpotifyAuthorization.Response.Success)
    }
  }

  @Test
  fun `authorize - when authorization denied - returns Failure`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Error.Denied
      },
    )

    launchInFragment { fragment ->
      val response = spotifyAuthorization.prepareAuthorization(fragment)
        .authorize(fragment.requireContext())

      assertThat(response).isEqualTo(SpotifyAuthorization.Response.Failure)
    }
  }

  @Test
  fun `logout - when authorized - logs you out`() = runTest {
    val handyAuth = FakeHandyAuth().apply {
      expectedAuthResult = HandyAuth.Result.Authorized
    }
    val spotifyAuthorization = SpotifyAuthorizationImpl(handyAuth)

    spotifyAuthorization.logout()

    assertThat(handyAuth.isAuthorized).isFalse()
  }

  @Test
  fun `getAuthorizationHeader - when authorized - returns auth header`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Authorized
      },
    )

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment)
        .authorize(fragment.requireContext())
    }

    assertThat(spotifyAuthorization.getAuthorizationHeader()).isEqualTo("Fake test-token")
  }

  @Test
  fun `getAuthorizationHeader - when not authorized - throws an error`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Error.Denied
      },
    )

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment)
        .authorize(fragment.requireContext())
    }

    try {
      spotifyAuthorization.getAuthorizationHeader()
      assert(false) { "Expected error not thrown" }
    } catch (e: Throwable) {
      assert(true)
    }
  }

  /**
   * Syntactically pleasant way ot launching a suspend function from an anonymous Fragment.
   */
  private fun launchInFragment(launchOnFragment: suspend (Fragment) -> Unit) {
    with(launchFragment<Fragment>()) {
      onFragment {
        it.lifecycleScope.launch {
          launchOnFragment(it)
        }
      }
    }
  }
}
