package com.evanisnor.freshwaves.debugmenu

import androidx.lifecycle.ViewModel
import com.evanisnor.freshwaves.BuildConfig
import com.evanisnor.freshwaves.debugmenu.items.AppInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class DebugMenuViewModel @Inject constructor() : ViewModel() {

    fun appInformation(): AppInformation = AppInformation(
        version = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        builtOn = Instant.ofEpochMilli(BuildConfig.BUILD_TIMESTAMP)
    )

}