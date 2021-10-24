package com.evanisnor.freshwaves.features.updater

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdaterRepository @Inject constructor() {

    private val _state: MutableStateFlow<UpdaterState> = MutableStateFlow(UpdaterState.Idle)
    val state: StateFlow<UpdaterState>
        get() = _state.asStateFlow()

    internal suspend fun updateState(updaterState: UpdaterState) {
        _state.emit(updaterState)
    }

}