package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.model.*
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate

class FakeSpotifyAPIService(
    private val behaviorDelegate: BehaviorDelegate<SpotifyAPIService>
) : SpotifyAPIService {

    var user: PrivateUserObject = PrivateUserObject(
        id = "0000000",
        email = "user@freshwaves.com",
        displayName = "User",
        country = "CA"
    )

    var topArtists: PagingObject<ArtistObject>? = null
    var artistAlbums: PagingObject<AlbumObject>? = null
    var albumTracks: PagingObject<TrackObject>? = null

    override fun getUserProfile(accessToken: String): Call<PrivateUserObject> =
        behaviorDelegate.returningResponse(user).getUserProfile(accessToken)

    override fun getTopArtists(
        accessToken: String,
        limit: Int,
        offset: Int
    ): Call<PagingObject<ArtistObject>> =
        behaviorDelegate.returningResponse(topArtists)
            .getTopArtists(accessToken, limit, offset)

    override fun getArtistAlbums(
        accessToken: String,
        artistId: String,
        market: String,
        includeGroups: String
    ): Call<PagingObject<AlbumObject>> =
        behaviorDelegate.returningResponse(artistAlbums)
            .getArtistAlbums(accessToken, artistId, market, includeGroups)

    override fun getAlbumTracks(
        accessToken: String,
        albumId: String
    ): Call<PagingObject<TrackObject>> =
        behaviorDelegate.returningResponse(albumTracks).getAlbumTracks(accessToken, albumId)
}