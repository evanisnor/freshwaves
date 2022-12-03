package com.evanisnor.freshwaves.spotify.auth

import app.cash.turbine.test
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.ext.launchInFragment
import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import com.evanisnor.handyauth.client.HandyAuth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
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
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
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
      val response = spotifyAuthorization.prepareAuthorization(fragment).authorize()

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
      val response = spotifyAuthorization.prepareAuthorization(fragment).authorize()

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
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
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
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
    }

    try {
      spotifyAuthorization.getAuthorizationHeader()
      assert(false) { "Expected error not thrown" }
    } catch (e: Throwable) {
      assert(true)
    }
  }

  @Test
  fun `latestResponse - when not authorised - returns Failure`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(FakeHandyAuth())

    spotifyAuthorization.latestResponse.test {
      assertThat(awaitItem()).isEqualTo(SpotifyAuthorization.Response.Failure)
    }
  }

  @Test
  fun `latestResponse - when authorised - returns Success`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(FakeHandyAuth().apply { authorize() })

    spotifyAuthorization.latestResponse.test {
      assertThat(awaitItem()).isEqualTo(SpotifyAuthorization.Response.Success)
    }
  }

  @Test
  fun `latestResponse - when user logs in successfully - returns Success`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Authorized
      },
    )

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
    }

    spotifyAuthorization.latestResponse.test {
      assertThat(awaitItem()).isEqualTo(SpotifyAuthorization.Response.Success)
    }
  }

  @Test
  fun `latestResponse - when user log in fails - returns Failure`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        expectedAuthResult = HandyAuth.Result.Error.Denied
      },
    )

    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
    }

    spotifyAuthorization.latestResponse.test {
      assertThat(awaitItem()).isEqualTo(SpotifyAuthorization.Response.Failure)
    }
  }

  @Test
  fun `latestResponse - when user logs out - returns Failure`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        authorize()
      },
    )

    spotifyAuthorization.logout()

    spotifyAuthorization.latestResponse.test {
      assertThat(awaitItem()).isEqualTo(SpotifyAuthorization.Response.Failure)
    }
  }

  @Test
  fun `latestResponse - when user logs out and logs back in - returns Success`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply {
        authorize()
      },
    )

    spotifyAuthorization.logout()
    launchInFragment { fragment ->
      spotifyAuthorization.prepareAuthorization(fragment).authorize()
    }

    spotifyAuthorization.latestResponse.test {
      assertThat(awaitItem()).isEqualTo(SpotifyAuthorization.Response.Success)
    }
  }
}
