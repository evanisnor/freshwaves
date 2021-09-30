package com.evanisnor.freshwaves.spotify.repository

import android.content.SharedPreferences
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Named

class SpotifyUserRepository @Inject constructor(
    private val spotifyNetworkRepository: SpotifyNetworkRepository,
    @Named("UserSettings") private val userSettings: SharedPreferences
) {

    fun getUserMarket() = userSettings.getString("country", "") ?: ""

    fun updateUserProfile(
        onFinished: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Executors.newSingleThreadExecutor().execute {

            spotifyNetworkRepository.getUserProfile(
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