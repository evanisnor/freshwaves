package com.evanisnor.freshwaves.fakes

import androidx.activity.ComponentActivity
import com.evanisnor.handyauth.client.HandyAccessToken
import com.evanisnor.handyauth.client.HandyAuth

class FakeHandyAuth : HandyAuth {

  override var isAuthorized: Boolean = true

  override suspend fun authorize(callingActivity: ComponentActivity) =
    HandyAuth.Result.Authorized

  override suspend fun accessToken(): HandyAccessToken = HandyAccessToken(
    token = "test-token",
    tokenType = "Fake",
  )

  override suspend fun logout() {
    isAuthorized = false
  }
}
