package com.evanisnor.freshwaves.deps.handyauth

import androidx.activity.ComponentActivity
import com.evanisnor.handyauth.client.HandyAccessToken
import com.evanisnor.handyauth.client.HandyAuth

class FakeHandyAuth : HandyAuth {

  private var loggedIn: Boolean = false
  var expectedAuthResult: HandyAuth.Result = HandyAuth.Result.Error.UnknownError

  override val isAuthorized: Boolean
    get() = loggedIn && expectedAuthResult == HandyAuth.Result.Authorized

  /**
   * Used to simplify most tests
   */
  internal fun authorize() {
    expectedAuthResult = HandyAuth.Result.Authorized
    loggedIn = true
  }

  override fun authorize(
      callingActivity: ComponentActivity,
      resultCallback: (HandyAuth.Result) -> Unit,
  ) {
    loggedIn = expectedAuthResult == HandyAuth.Result.Authorized
    resultCallback(expectedAuthResult)
  }

  override suspend fun accessToken(): HandyAccessToken =
    if (!isAuthorized) {
      throw IllegalStateException("Call to retrieve access token when not authorized")
    } else {
      HandyAccessToken(
        token = "test-token",
        tokenType = "Fake"
      )
    }

  override suspend fun logout() {
    loggedIn = false
  }
}