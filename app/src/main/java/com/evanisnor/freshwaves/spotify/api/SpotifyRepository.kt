package com.evanisnor.freshwaves.spotify.api

import com.evanisnor.freshwaves.spotify.repository.SpotifyAlbumRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyArtistRepository
import com.evanisnor.freshwaves.spotify.repository.SpotifyUserRepository

interface SpotifyRepository : SpotifyAlbumRepository, SpotifyArtistRepository, SpotifyUserRepository
