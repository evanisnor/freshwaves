package com.evanisnor.freshwaves.spotify

import app.cash.turbine.test
import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.spotify.api.SpotifyRepository
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import com.evanisnor.freshwaves.spotify.network.SpotifyNetworkRepository
import com.evanisnor.freshwaves.spotify.network.model.ArtistObject
import com.evanisnor.freshwaves.spotify.network.model.PrivateUserObject
import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository
import com.evanisnor.freshwaves.user.UserProfile
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
class SpotifyRepositoryImplTest {

    private lateinit var handyAuth: FakeHandyAuth
    private lateinit var spotifyCacheDao: FakeSpotifyCacheDao
    private lateinit var spotifyAPIService: FakeSpotifyAPIService
    private lateinit var spotifyRepository: SpotifyRepository

    @Before
    fun setup() {
        handyAuth = FakeHandyAuth().apply {
            authorize()
        }
        spotifyAPIService = FakeSpotifyAPIService()
        spotifyCacheDao = FakeSpotifyCacheDao()
        val spotifyNetworkRepository = SpotifyNetworkRepository(
            SpotifyAuthorizationImpl(handyAuth),
            spotifyAPIService
        )

        spotifyRepository = SpotifyRepositoryImpl(
            SpotifyUserRepository(spotifyNetworkRepository),
            SpotifyArtistRepository(spotifyNetworkRepository, spotifyCacheDao),
            SpotifyAlbumRepository(spotifyNetworkRepository, spotifyCacheDao),
        )
    }

    @Test
    fun `userProfile - when authorized - returns user profile`() = runTest {
        spotifyAPIService.privateUser = PrivateUserObject(
            id = "user-004",
            email = "user004@email.com",
            displayName = "Test",
            country = "X"
        )

        val userProfile = spotifyRepository.userProfile()

        assertThat(userProfile).isEqualTo(
            UserProfile(
                id = "user-004",
                email = "user004@email.com",
                name = "Test",
                country = "X"
            )
        )
    }

    @Test
    fun `getTopArtists - when authorized - returns top artists`() = runTest {
        spotifyCacheDao._insertArtist(Artist("09", "Artist 09"))
        spotifyCacheDao._insertArtist(Artist("10", "Artist 10"))

        val topArtists = spotifyRepository.getTopArtists()

        assertThat(topArtists).isEqualTo(
            listOf(
                Artist("09", "Artist 09"),
                Artist("10", "Artist 10"),
            )
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

            spotifyRepository.updateTopArtists(numberOfArtists = 2, artistsPerPage = 2)
            val topArtists = spotifyRepository.getTopArtists()

            assertThat(topArtists).isEqualTo(
                listOf(
                    Artist("01", "Artist 01"),
                    Artist("02", "Artist 02"),
                )
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

            spotifyRepository.updateTopArtists(numberOfArtists = 2, artistsPerPage = 1)
            val topArtists = spotifyRepository.getTopArtists()

            assertThat(topArtists).isEqualTo(
                listOf(
                    Artist("01", "Artist 01"),
                    Artist("02", "Artist 02"),
                )
            )
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
                )
            )
        )

        spotifyRepository.getLatestAlbums().test {
            assertThat(awaitItem()).isEqualTo(
                listOf(
                    Album(
                        id = 0,
                        artist = artist01,
                        name = "Album 0",
                        releaseDate = Instant.ofEpochSecond(500L),
                    )
                )
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
                )
            )
        )

        val albums = spotifyRepository.getAlbumsReleasedAfter(Instant.ofEpochSecond(600L))
        assertThat(albums).isEqualTo(
            listOf(
                Album(
                    id = 1,
                    artist = artist01,
                    name = "Album 1",
                    releaseDate = Instant.ofEpochSecond(600L),
                )
            )
        )
    }

}