package com.evanisnor.freshwaves.spotify.network

import okhttp3.Interceptor
import okhttp3.Response

class RateLimitInterceptor(private val delayMs: Long) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    var response = chain.proceed(chain.request())
    if (!response.isSuccessful && response.code == 429) {
      Thread.sleep(delayMs)
      response = chain.proceed(chain.request())
    }
    return response
  }
}
