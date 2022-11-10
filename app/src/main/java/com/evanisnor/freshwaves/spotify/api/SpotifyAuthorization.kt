package com.evanisnor.freshwaves.spotify.api

import androidx.activity.ComponentActivity

/**
 * Authorization interface for Spotify
 */
interface SpotifyAuthorization {

    /**
     * Authorization response types
     */
    sealed interface Response {
        object Success : Response
        object Failure : Response
    }

    val isAuthorized: Boolean

    /**
     * Begin the authorization flow.
     * @param [activity] Activity used to launch the login flow
     * @return [Response]
     */
    suspend fun authorize(activity: ComponentActivity) : Response

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