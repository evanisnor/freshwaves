# Fresh Waves

Stay up to date with the latest releases from your favorite artists.

Powered by Spotify ![](img/spotify.png)

## Design

### Use Case: Latest Albums

Show a list of the latest albums from your favorite artists

### Fetch Process

1. Daily at 6am
2. Get a User's Top Artists
    - GET https://api.spotify.com/v1/me/top/artists
3. For each artist, get each album
    - GET https://api.spotify.com/v1/artists/{id}/albums
4. For each album, get each track
    - GET https://api.spotify.com/v1/albums/{id}/tracks

### User Experience

1. Main Activity
    4. Latest Albums
        1. Show 30 most recent albums
        2. Tap on an album -> Album Activity
2. Album Activity
    1. Large image
    2. Artist name
    3. Release Date
    4. List of tracks
    5. Genre
3. Fresh Notification
    1. After fetch, show notification if new releases are found
