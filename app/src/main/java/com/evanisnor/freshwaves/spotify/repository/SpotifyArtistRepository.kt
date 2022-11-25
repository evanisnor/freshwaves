package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.backend.BackendAPIRepository
import com.evanisnor.freshwaves.spotify.cache.SpotifyCacheDao
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.ceil

class SpotifyArtistRepository @Inject constructor(
  private val spotifyNetworkRepository: SpotifyNetworkRepository,
  private val spotifyCacheDao: SpotifyCacheDao
) {

  suspend fun getTopArtists(): List<Artist> = spotifyCacheDao.readArtists()

  suspend fun updateTopArtists(numberOfArtists: Int, artistsPerPage: Int = 30) {
    val pages = ceil(numberOfArtists / artistsPerPage.toFloat()).toInt()
    var offset = 0

    for (i in 0 until pages) {
      val artists = spotifyNetworkRepository.topArtists(artistsPerPage, offset)
      Timber.d("Fetched ${artists.size} artists")
      spotifyCacheDao.insertArtists(artists)
      Timber.d("Inserted ${artists.size} artists")
      offset += artists.size
    }
  }
}
