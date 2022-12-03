package com.evanisnor.freshwaves.user

import com.evanisnor.freshwaves.spotify.api.SpotifyAuthorization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserStateRepository @Inject constructor(
  private val spotifyAuthorization: SpotifyAuthorization,
) {

  sealed interface State {
    object NoUser : State
    object LoggedIn : State
  }

  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  init {
    scope.launch {
      spotifyAuthorization.latestResponse.collect {
        when (it) {
          is SpotifyAuthorization.Response.Success -> _currentState.emit(State.LoggedIn)
          is SpotifyAuthorization.Response.Failure -> _currentState.emit(State.NoUser)
        }
      }
    }
  }

  private val _currentState: MutableStateFlow<State> = MutableStateFlow(
    if (spotifyAuthorization.isAuthorized) {
      State.LoggedIn
    } else {
      State.NoUser
    },
  )
  val currentState: Flow<State> = _currentState.asStateFlow()
}
