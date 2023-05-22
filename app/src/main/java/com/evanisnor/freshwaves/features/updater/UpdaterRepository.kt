package com.evanisnor.freshwaves.features.updater

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.evanisnor.freshwaves.user.UserStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

@Module
@InstallIn(SingletonComponent::class)
abstract class UpdaterRepositoryModule {
  @Singleton
  @Binds
  abstract fun bindUpdaterRepository(impl: RealUpdaterRepository): UpdaterRepository
}

/**
 * Repository that holds [UpdaterState] and information about updater executions.
 */
interface UpdaterRepository {
  val state: StateFlow<UpdaterState>

  suspend fun updateState(updaterState: UpdaterState)

  suspend fun lastKnownState(): UpdaterState

  suspend fun updateLastRun(lastRun: Instant)

  suspend fun lastRunOn(): Instant?

  suspend fun updateNextRun(nextRun: Instant)

  suspend fun nextRunOn(): Instant?
}

/**
 * Real implementation of [UpdaterRepository]
 */
class RealUpdaterRepository @Inject constructor(
  @Named("UpdaterMeta") private val updaterMeta: DataStore<Preferences>,
  userStateRepository: UserStateRepository,
) : UpdaterRepository {

  companion object {
    private val LAST_KNOWN_STATE_KEY = stringPreferencesKey("lastKnownState")
    private val LAST_RUN_TIMESTAMP = longPreferencesKey("lastRunTimestamp")
    private val NEXT_RUN_TIMESTAMP = longPreferencesKey("nextRunTimestamp")
  }

  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private val _state: MutableStateFlow<UpdaterState> = MutableStateFlow(UpdaterState.Idle)
  override val state: StateFlow<UpdaterState>
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

  override suspend fun updateState(updaterState: UpdaterState) {
    if (updaterState != UpdaterState.Idle && updaterState != UpdaterState.Unknown) {
      updaterMeta.edit { meta ->
        meta[LAST_KNOWN_STATE_KEY] = updaterState.name
      }
    }
    _state.emit(updaterState)
  }

  override suspend fun lastKnownState(): UpdaterState {
    val lastKnownStateName = updaterMeta.data.firstOrNull()?.get(LAST_KNOWN_STATE_KEY)
    return if (lastKnownStateName != null) {
      UpdaterState.valueOf(lastKnownStateName)
    } else {
      UpdaterState.Unknown
    }
  }

  override suspend fun updateLastRun(lastRun: Instant) {
    updaterMeta.edit { meta ->
      meta[LAST_RUN_TIMESTAMP] = lastRun.toEpochMilli()
    }
  }

  override suspend fun lastRunOn(): Instant? {
    val lastRunTimestampMs = updaterMeta.data.firstOrNull()?.get(LAST_RUN_TIMESTAMP)
    return if (lastRunTimestampMs != null) {
      Instant.ofEpochMilli(lastRunTimestampMs)
    } else {
      null
    }
  }

  override suspend fun updateNextRun(nextRun: Instant) {
    updaterMeta.edit { meta ->
      meta[NEXT_RUN_TIMESTAMP] = nextRun.toEpochMilli()
    }
  }

  override suspend fun nextRunOn(): Instant? {
    val nextRunTimestampMs = updaterMeta.data.firstOrNull()?.get(NEXT_RUN_TIMESTAMP)
    return if (nextRunTimestampMs != null) {
      Instant.ofEpochMilli(nextRunTimestampMs)
    } else {
      null
    }
  }
}
