package com.evanisnor.freshwaves.spotify.api

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

/**
 * Authorization interface for Spotify
 */
interface SpotifyAuthorization {

  /**
   * Login Flow State
   */
  interface PendingAuthorization {
    suspend fun authorize(context: Context): Response
  }

  /**
   * Authorization response types
   */
  sealed interface Response {
    object Success : Response
    object Failure : Response
  }

  val isAuthorized: Boolean

  /**
   * Prepare the authorization flow. Call [PendingAuthorization.authorize] when you want to
   * launch the authorization flow.
   */
  suspend fun prepareAuthorization(fragment: Fragment): PendingAuthorization

  /**
   * Begin the authorization flow.
   * @param [activity] Activity used to launch the login flow
   * @return [Response]
   */
  suspend fun authorize(activity: ComponentActivity): Response

  /**
   * Log out of Spotify by resetting auth cache
   */
  suspend fun logout()

  /**
   * Get the latest authorization header value for API requests
   */
  suspend fun getAuthorizationHeader(): String

  companion object {

    /**
     * Used for LocalBroadcast intent until I refactor this into something nicer.
     */
    const val authorizationSuccessfulAction = "authorization-successful"
  }
}
