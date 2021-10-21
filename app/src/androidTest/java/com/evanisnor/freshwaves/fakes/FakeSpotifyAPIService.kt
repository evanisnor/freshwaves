package com.evanisnor.freshwaves.fakes

import com.evanisnor.freshwaves.spotify.network.SpotifyAPIService
import com.evanisnor.freshwaves.spotify.network.model.*
import retrofit2.mock.BehaviorDelegate
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Fake API Service for Spotify network calls
 */
class FakeSpotifyAPIService(
    private val behaviorDelegate: BehaviorDelegate<SpotifyAPIService>
) : SpotifyAPIService {


    // region UserProfile

    var user: PrivateUserObject? = null

    override suspend fun getUserProfile(accessToken: String): PrivateUserObject =
        behaviorDelegate.returningResponse(user).getUserProfile(accessToken)

    // endregion

    // region Artists

    private val topArtists: Queue<PagingObject<ArtistObject>> = ConcurrentLinkedQueue()

    fun queueArtists(artistObjects: PagingObject<ArtistObject>) {
        topArtists.offer(artistObjects)
    }

    override suspend fun getTopArtists(
        accessToken: String,
        limit: Int,
        offset: Int
    ): PagingObject<ArtistObject> = if (topArtists.isEmpty()) {
        behaviorDelegate.returningResponse(PagingObject<ArtistObject>(emptyList()))
    } else {
        behaviorDelegate.returningResponse(topArtists.remove())
    }.getTopArtists(accessToken, limit, offset)

    // endregion

    // region Albums

    private val artistAlbums: ConcurrentHashMap<String, Queue<PagingObject<AlbumObject>>> =
        ConcurrentHashMap()

    fun queueAlbums(artistId: String, albumObjects: PagingObject<AlbumObject>) {
        artistAlbums[artistId] = artistAlbums.getOrDefault(artistId, ConcurrentLinkedQueue())
            .apply {
                offer(albumObjects)
            }
    }

    override suspend fun getArtistAlbums(
        accessToken: String,
        artistId: String,
        market: String,
        limit: Int,
        includeGroups: String
    ): PagingObject<AlbumObject> = if (artistAlbums.containsKey(artistId)) {
        behaviorDelegate.returningResponse(artistAlbums[artistId]?.remove())
    } else {
        behaviorDelegate.returningResponse(PagingObject<AlbumObject>(emptyList()))
    }.getArtistAlbums(accessToken, artistId, market, limit, includeGroups)

    // endregion

    // region Tracks

    private val albumTracks: ConcurrentHashMap<String, Queue<PagingObject<TrackObject>>> =
        ConcurrentHashMap()

    fun queueTracks(albumId: String, trackObjects: PagingObject<TrackObject>) {
        albumTracks[albumId] = albumTracks.getOrDefault(albumId, ConcurrentLinkedQueue()).apply {
            offer(trackObjects)
        }
    }

    override suspend fun getAlbumTracks(
        accessToken: String,
        albumId: String
    ): PagingObject<TrackObject> = if (albumTracks.contains(albumId)) {
        behaviorDelegate.returningResponse(albumTracks[albumId]?.remove())
    } else {
        behaviorDelegate.returningResponse(PagingObject<TrackObject>(emptyList()))
    }.getAlbumTracks(accessToken, albumId)

    // endregion Tracks
}