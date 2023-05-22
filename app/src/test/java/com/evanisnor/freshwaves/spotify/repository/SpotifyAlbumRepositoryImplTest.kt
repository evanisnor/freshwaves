package com.evanisnor.freshwaves.spotify.repository

import app.cash.turbine.test
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.FakeSpotifyAPIService
import com.evanisnor.freshwaves.spotify.FakeSpotifyCacheDao
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.cache.model.entities.Track
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SpotifyAlbumRepositoryImplTest {

  private lateinit var spotifyCacheDao: FakeSpotifyCacheDao
  private lateinit var spotifyAlbumRepository: SpotifyAlbumRepositoryImpl

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

    spotifyAlbumRepository = SpotifyAlbumRepositoryImpl(spotifyNetworkRepository, spotifyCacheDao)
  }

  @Test
  fun `getLatestAlbums - when albums are cached - returns latest albums`() = runTest {
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

    spotifyAlbumRepository.getLatestAlbums(limit = 1).test {
      assertThat(awaitItem()).isEqualTo(
        listOf(
          Album(
            id = 1,
            artist = artist01,
            name = "Album 1",
            releaseDate = Instant.ofEpochSecond(600L),
          ),
        ),
      )
    }
  }

  @Test
  fun `getAlbumsReleasedAfter - when albums are cached - returns latest albums`() = runTest {
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

    val albums = spotifyAlbumRepository.getAlbumsReleasedAfter(Instant.ofEpochSecond(600L))
    assertThat(albums).isEqualTo(
      listOf(
        Album(
          id = 1,
          artist = artist01,
          name = "Album 1",
          releaseDate = Instant.ofEpochSecond(600L),
        ),
      ),
    )
  }

  @Test
  fun `getLatestAlbumsMissingTracks - when some albums don't have tracks - return albums missing tracks`() =
    runTest {
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
      spotifyCacheDao.insertTracks(
        listOf(
          Track(
            0,
            "0",
            1,
            0,
            1,
            "A song",
            "",
            Duration.ofMinutes(3L),
          ),
        ),
      )

      val albumsMissingTracks = spotifyAlbumRepository.getLatestAlbumsMissingTracks()

      assertThat(albumsMissingTracks).isEqualTo(
        listOf(
          Album(
            id = 0,
            artist = artist01,
            name = "Album 0",
            releaseDate = Instant.ofEpochSecond(500L),
          ),
        ),
      )
    }
}
