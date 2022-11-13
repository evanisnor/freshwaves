package com.evanisnor.freshwaves.fakes

import androidx.activity.ComponentActivity
import com.evanisnor.handyauth.client.HandyAccessToken
import com.evanisnor.handyauth.client.HandyAuth

class FakeHandyAuth : HandyAuth {

  override var isAuthorized: Boolean = true

  override fun authorize(
    callingActivity: ComponentActivity,
    resultCallback: (HandyAuth.Result) -> Unit,
  ) {
    resultCallback(HandyAuth.Result.Authorized)
  }

  override suspend fun accessToken(): HandyAccessToken = HandyAccessToken(
    token = "test-token",
    tokenType = "Fake",
  )

  override suspend fun logout() {
    isAuthorized = false
  }
}
