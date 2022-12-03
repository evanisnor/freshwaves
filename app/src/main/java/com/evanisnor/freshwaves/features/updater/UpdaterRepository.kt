package com.evanisnor.freshwaves.features.updater

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.evanisnor.freshwaves.user.UserStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UpdaterRepository @Inject constructor(
  @Named("UpdaterMeta") private val updaterMeta: DataStore<Preferences>,
  userStateRepository: UserStateRepository,
) {

  companion object {
    private val lastKnownStateKey = stringPreferencesKey("lastKnownState")
    private val lastRunTimestamp = longPreferencesKey("lastRunTimestamp")
    private val nextRunTimestamp = longPreferencesKey("nextRunTimestamp")
  }

  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private val _state: MutableStateFlow<UpdaterState> = MutableStateFlow(UpdaterState.Idle)
  val state: StateFlow<UpdaterState>
    get() = _state.asStateFlow()

  init {
    scope.launch {
      userStateRepository.currentState.collect { it ->
        if (it == UserStateRepository.State.NoUser) {
          updaterMeta.edit { prefs ->
            prefs.clear()
          }
        }
      }
    }
  }

  internal suspend fun updateState(updaterState: UpdaterState) {
    if (updaterState != UpdaterState.Idle && updaterState != UpdaterState.Unknown) {
      updaterMeta.edit { meta ->
        meta[lastKnownStateKey] = updaterState.name
      }
    }
    _state.emit(updaterState)
  }

  suspend fun lastKnownState(): UpdaterState {
    val lastKnownStateName = updaterMeta.data.firstOrNull()?.get(lastKnownStateKey)
    return if (lastKnownStateName != null) {
      UpdaterState.valueOf(lastKnownStateName)
    } else {
      UpdaterState.Unknown
    }
  }

  suspend fun updateLastRun(lastRun: Instant) {
    updaterMeta.edit { meta ->
      meta[lastRunTimestamp] = lastRun.toEpochMilli()
    }
  }

  suspend fun lastRunOn(): Instant? {
    val lastRunTimestampMs = updaterMeta.data.firstOrNull()?.get(lastRunTimestamp)
    return if (lastRunTimestampMs != null) {
      Instant.ofEpochMilli(lastRunTimestampMs)
    } else {
      null
    }
  }

  suspend fun updateNextRun(nextRun: Instant) {
    updaterMeta.edit { meta ->
      meta[nextRunTimestamp] = nextRun.toEpochMilli()
    }
  }

  suspend fun nextRunOn(): Instant? {
    val nextRunTimestampMs = updaterMeta.data.firstOrNull()?.get(nextRunTimestamp)
    return if (nextRunTimestampMs != null) {
      Instant.ofEpochMilli(nextRunTimestampMs)
    } else {
      null
    }
  }
}
