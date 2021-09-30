package com.evanisnor.freshwaves.spotify.repository

import android.content.Context
import android.content.SharedPreferences
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import java.util.concurrent.Executors

class SpotifyUserRepository(
    private val spotifyNetworkRepository: SpotifyNetworkRepository,
    private val userSettings: SharedPreferences
) {

    fun getUserMarket() = userSettings.getString("country", "") ?: ""

    fun updateUserProfile(
        context: Context,
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Executors.newSingleThreadExecutor().execute {

            spotifyNetworkRepository.getUserProfile(
                context = context,
                onResult = { userProfile ->
                    userSettings.edit()
                        .putString("id", userProfile.id)
                        .putString("name", userProfile.name)
                        .putString("email", userProfile.email)
                        .putString("country", userProfile.country)
                        .apply()
                    onFinished()
                },
                onError = onError
            )

        }
    }

    // endregion


}