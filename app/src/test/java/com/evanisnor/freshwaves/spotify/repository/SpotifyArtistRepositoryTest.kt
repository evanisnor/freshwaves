package com.evanisnor.freshwaves.spotify.repository

import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.FakeSpotifyAPIService
import com.evanisnor.freshwaves.spotify.FakeSpotifyCacheDao
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SpotifyArtistRepositoryTest {

  private lateinit var spotifyCacheDao: FakeSpotifyCacheDao
  private lateinit var spotifyAPIService: FakeSpotifyAPIService
  private lateinit var spotifyArtistRepository: SpotifyArtistRepository

  @Before
  fun setup() {
    spotifyAPIService = FakeSpotifyAPIService()
    spotifyCacheDao = FakeSpotifyCacheDao()
    val spotifyNetworkRepository = SpotifyNetworkRepository(
      SpotifyAuthorizationImpl(
        FakeHandyAuth().apply {
          authorize()
        },
      ),
      spotifyAPIService,
    )

    spotifyArtistRepository = SpotifyArtistRepository(
      spotifyNetworkRepository,
      spotifyCacheDao,
    )
  }

  @Test
  fun `getTopArtists - when authorized - returns top artists`() = runTest {
    spotifyCacheDao._insertArtist(Artist("09", "Artist 09"))
    spotifyCacheDao._insertArtist(Artist("10", "Artist 10"))

    val topArtists = spotifyArtistRepository.getTopArtists()

    assertThat(topArtists).isEqualTo(
      listOf(
        Artist("09", "Artist 09"),
        Artist("10", "Artist 10"),
      ),
    )
  }

  @Test
  fun `updateTopArtists - when querying single page of results - returns top artists`() =
    runTest {
      spotifyAPIService.topArtists = listOf(
        ArtistObject("01", "Artist 01"),
        ArtistObject("02", "Artist 02"),
        ArtistObject("03", "Artist 03"),
      )

      spotifyArtistRepository.updateTopArtists(numberOfArtists = 2, artistsPerPage = 2)
      val topArtists = spotifyArtistRepository.getTopArtists()

      assertThat(topArtists).isEqualTo(
        listOf(
          Artist("01", "Artist 01"),
          Artist("02", "Artist 02"),
        ),
      )
    }

  @Test
  fun `updateTopArtists - when querying two pages of results - returns top artists`() =
    runTest {
      spotifyAPIService.topArtists = listOf(
        ArtistObject("01", "Artist 01"),
        ArtistObject("02", "Artist 02"),
        ArtistObject("03", "Artist 03"),
      )

      spotifyArtistRepository.updateTopArtists(numberOfArtists = 2, artistsPerPage = 1)
      val topArtists = spotifyArtistRepository.getTopArtists()

      assertThat(topArtists).isEqualTo(
        listOf(
          Artist("01", "Artist 01"),
          Artist("02", "Artist 02"),
        ),
      )
    }
}
