package com.evanisnor.freshwaves.fakes

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.evanisnor.handyauth.client.HandyAccessToken
import com.evanisnor.handyauth.client.HandyAuth

class FakeHandyAuth : HandyAuth {

  override var isAuthorized: Boolean = true

  override suspend fun prepareAuthorization(callingFragment: Fragment): HandyAuth.PendingAuthorization =
    object : HandyAuth.PendingAuthorization {
      override suspend fun authorize(): HandyAuth.Result = authorize(callingFragment)
    }

  override suspend fun prepareAuthorization(callingActivity: ComponentActivity): HandyAuth.PendingAuthorization =
    object : HandyAuth.PendingAuthorization {
      override suspend fun authorize(): HandyAuth.Result = authorize(callingActivity)
    }

  override suspend fun authorize(callingFragment: Fragment): HandyAuth.Result =
    HandyAuth.Result.Authorized

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
