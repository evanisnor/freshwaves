package com.evanisnor.freshwaves.spotify.auth

import androidx.activity.ComponentActivity
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.handyauth.client.HandyAuth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SpotifyAuthorizationImplTest {

  private val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()

  @Test
  fun `isAuthorized - when HandyAuth is authorized - returns true`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Authorized
      },
    )

    spotifyAuthorization.authorize(activity)

    assertThat(spotifyAuthorization.isAuthorized).isTrue()
  }

  @Test
  fun `isAuthorized - when HandyAuth is not authorized - returns false`() = runTest {
    val handyAuth = FakeHandyAuth().apply {
      expectedAuthResult = HandyAuth.Result.Error.Denied
    }
    val spotifyAuthorization = SpotifyAuthorizationImpl(handyAuth)

    spotifyAuthorization.authorize(activity)

    assertThat(spotifyAuthorization.isAuthorized).isFalse()
  }

  @Test
  fun `authorize - when authorization granted - returns Success`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Authorized
      },
    )

    val response = spotifyAuthorization.authorize(activity)

    assertThat(response).isEqualTo(SpotifyAuthorization.Response.Success)
  }

  @Test
  fun `authorize - when authorization denied - returns Failure`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Error.Denied
      },
    )

    val response = spotifyAuthorization.authorize(activity)

    assertThat(response).isEqualTo(SpotifyAuthorization.Response.Failure)
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

    spotifyAuthorization.authorize(activity)

    assertThat(spotifyAuthorization.getAuthorizationHeader()).isEqualTo("Fake test-token")
  }

  @Test
  fun `getAuthorizationHeader - when not authorized - throws an error`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Error.Denied
      },
    )

    spotifyAuthorization.authorize(activity)

    try {
      spotifyAuthorization.getAuthorizationHeader()
      assert(false) { "Expected error not thrown" }
    } catch (e: Throwable) {
      assert(true)
    }
  }
}
