package com.evanisnor.freshwaves.spotify.api

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow

/**
 * Authorization interface for Spotify
 */
interface SpotifyAuthorization {
  /**
   * Login Flow State
   */
  interface PendingAuthorization {
    suspend fun authorize(): Response
  }

  /**
   * Authorization response types
   */
  sealed interface Response {
    data object Success : Response

    data object Failure : Response
  }

  val isAuthorized: Boolean

  val latestResponse: Flow<Response>

  /**
   * Prepare the authorization flow. Call [PendingAuthorization.authorize] when you want to
   * launch the authorization flow.
   */
  suspend fun prepareAuthorization(fragment: Fragment): PendingAuthorization

  /**
   * Log out of Spotify by resetting auth cache
   */
  suspend fun logout()

  /**
   * Get the latest authorization header value for API requests
   */
  suspend fun getAuthorizationHeader(): String
}
