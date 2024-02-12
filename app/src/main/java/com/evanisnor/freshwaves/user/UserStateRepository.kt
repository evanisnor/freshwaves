package com.evanisnor.freshwaves.user

import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStateRepository
  @Inject
  constructor(
    spotifyAuthorization: SpotifyAuthorization,
  ) {
    sealed interface State {
      data object NoUser : State

      data object LoggedIn : State
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentState: Flow<State> =
      spotifyAuthorization.latestResponse.mapLatest {
        when (it) {
          is SpotifyAuthorization.Response.Success -> State.LoggedIn
          is SpotifyAuthorization.Response.Failure -> State.NoUser
        }
      }
  }
