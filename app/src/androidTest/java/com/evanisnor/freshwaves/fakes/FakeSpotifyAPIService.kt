package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.model.*
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Fake API Service for Spotify network calls
 */
class FakeSpotifyAPIService(
    private val behaviorDelegate: BehaviorDelegate<SpotifyAPIService>
) : SpotifyAPIService {

    private val topArtists: Queue<PagingObject<ArtistObject>> = ConcurrentLinkedQueue()
    private val artistAlbums: Queue<PagingObject<AlbumObject>> = ConcurrentLinkedQueue()
    private val albumTracks: Queue<PagingObject<TrackObject>> = ConcurrentLinkedQueue()

    var user: PrivateUserObject? = null

    fun queueArtists(artistObjects: PagingObject<ArtistObject>) {
        topArtists.offer(artistObjects)
    }

    fun queueAlbums(albumObjects: PagingObject<AlbumObject>) {
        artistAlbums.offer(albumObjects)
    }

    fun queueTracks(trackObjects: PagingObject<TrackObject>) {
        albumTracks.offer(trackObjects)
    }

    // region Behavior Delegate

    override fun getUserProfile(accessToken: String): Call<PrivateUserObject> =
        behaviorDelegate.returningResponse(user).getUserProfile(accessToken)

    override fun getTopArtists(
        accessToken: String,
        limit: Int,
        offset: Int
    ): Call<PagingObject<ArtistObject>> = if (topArtists.isEmpty()) {
        behaviorDelegate.returningResponse(PagingObject<ArtistObject>(emptyList()))
    } else {
        behaviorDelegate.returningResponse(topArtists.remove())
    }.getTopArtists(accessToken, limit, offset)

    override fun getArtistAlbums(
        accessToken: String,
        artistId: String,
        market: String,
        includeGroups: String
    ): Call<PagingObject<AlbumObject>> = if (artistAlbums.isEmpty()) {
        behaviorDelegate.returningResponse(PagingObject<AlbumObject>(emptyList()))
    } else {
        behaviorDelegate.returningResponse(artistAlbums.remove())
    }.getArtistAlbums(accessToken, artistId, market, includeGroups)

    override fun getAlbumTracks(
        accessToken: String,
        albumId: String
    ): Call<PagingObject<TrackObject>> = if (albumTracks.isEmpty()) {
        behaviorDelegate.returningResponse(PagingObject<TrackObject>(emptyList()))
    } else {
        behaviorDelegate.returningResponse(albumTracks.remove())
    }.getAlbumTracks(accessToken, albumId)

    // endregion
}