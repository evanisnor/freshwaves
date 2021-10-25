package com.evanisnor.freshwaves.debugmenu

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.BuildConfig
import com.evanisnor.freshwaves.debugmenu.items.AppInformation
import com.evanisnor.freshwaves.features.updater.UpdaterRepository
import com.evanisnor.freshwaves.features.updater.UpdaterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class DebugMenuViewModel @Inject constructor(
    private val updaterRepository: UpdaterRepository
) : ViewModel() {

    fun appInformation(): AppInformation = AppInformation(
        version = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        builtOn = Instant.ofEpochMilli(BuildConfig.BUILD_TIMESTAMP)
    )

    suspend fun updaterStatus(): StateFlow<UpdaterState> = updaterRepository.state
    suspend fun updaterLastKnownState(): UpdaterState = updaterRepository.lastKnownState()
    suspend fun updaterLastRunOn(): Instant? = updaterRepository.lastRunOn()
    suspend fun updaterNextRunOn(): Instant? = updaterRepository.nextRunOn()

}