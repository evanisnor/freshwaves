package com.evanisnor.freshwaves

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.userSettings: DataStore<Preferences> by preferencesDataStore(name = "userSettings")