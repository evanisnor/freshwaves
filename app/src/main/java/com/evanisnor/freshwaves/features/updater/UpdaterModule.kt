package com.evanisnor.freshwaves.features.updater

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class UpdaterModule {

  @Provides
  fun workManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)

  @Provides
  fun localBroadcastManager(@ApplicationContext context: Context) =
    LocalBroadcastManager.getInstance(context)

  @Provides
  @Named("UpdaterMeta")
  fun updaterMetaDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
      corruptionHandler = null,
      migrations = listOf(),
      scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    ) {
      context.preferencesDataStoreFile("UpdaterMeta")
    }
}