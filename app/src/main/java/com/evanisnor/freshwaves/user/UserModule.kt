package com.evanisnor.freshwaves.user

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Named("UserSettings")
    fun userSettingsSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("userSettings", MODE_PRIVATE)
}