package com.evanisnor.freshwaves.spotify

import app.cash.turbine.test
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import com.evanisnor.freshwaves.user.UserStateRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SpotifyRepositoryTest {

  private lateinit var spotifyCacheDao: FakeSpotifyCacheDao
  private lateinit var spotifyUserRepository: SpotifyUserRepository
  private lateinit var spotifyArtistRepository: SpotifyArtistRepository
  private lateinit var spotifyAlbumRepository: SpotifyAlbumRepository

  @Before
  fun setup() {
    spotifyCacheDao = FakeSpotifyCacheDao()
    val spotifyNetworkRepository = SpotifyNetworkRepository(
      SpotifyAuthorizationImpl(
        FakeHandyAuth().apply {
          authorize()
        },
      ),
      FakeSpotifyAPIService(),
    )

    spotifyUserRepository = SpotifyUserRepository(spotifyNetworkRepository)
    spotifyArtistRepository = SpotifyArtistRepository(spotifyNetworkRepository, spotifyCacheDao)
    spotifyAlbumRepository = SpotifyAlbumRepository(spotifyNetworkRepository, spotifyCacheDao)
  }

  @Test
  fun `init - when user logs out - all data is deleted`() = runTest {
    val spotifyAuthorization = SpotifyAuthorizationImpl(
      FakeHandyAuth().apply { authorize() },
    )
    SpotifyRepositoryImpl(
      spotifyCacheDao.database,
      UserStateRepository(spotifyAuthorization),
      spotifyUserRepository,
      spotifyArtistRepository,
      spotifyAlbumRepository,
    )

    val artist01 = Artist("01", "Artist 01")
    spotifyCacheDao.insertArtists(listOf(artist01))

    spotifyAuthorization.logout()

    assertThat(spotifyCacheDao.readArtists()).isEmpty()
  }

  @Test
  fun `init - when user is authorized - data is available`() = runTest {
    val spotifyRepository = SpotifyRepositoryImpl(
      spotifyCacheDao.database,
      UserStateRepository(
        SpotifyAuthorizationImpl(
          FakeHandyAuth().apply { authorize() },
        ),
      ),
      spotifyUserRepository,
      spotifyArtistRepository,
      spotifyAlbumRepository,
    )

    val artist01 = Artist("01", "Artist 01")
    spotifyCacheDao.insertArtists(listOf(artist01))
    spotifyCacheDao.insertAlbums(
      listOf(
        Album(
          id = 0,
          artist = artist01,
          name = "Album 0",
          releaseDate = Instant.ofEpochSecond(500L),
        ),
        Album(
          id = 1,
          artist = artist01,
          name = "Album 1",
          releaseDate = Instant.ofEpochSecond(600L),
        ),
      ),
    )

    spotifyRepository.getLatestAlbums().test {
      assertThat(awaitItem()).isNotEmpty()
    }
  }
}
