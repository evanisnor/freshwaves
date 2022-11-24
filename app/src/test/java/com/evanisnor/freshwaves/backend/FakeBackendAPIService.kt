package com.evanisnor.freshwaves.backend

class FakeBackendAPIService : BackendAPIService {

  override suspend fun reportFavoriteArtists(authorization: String, artistList: ArtistList) {}
}
