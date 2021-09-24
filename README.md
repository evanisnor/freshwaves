# Fresh Waves

Stay up to date with the latest releases from your favorite artists.

Powered by Spotify ![](img/spotify.png)

## Design

### Use Case 1: Latest Singles

Show a list of the latest singles from your favorite artists

### Use Case 2: Latest Albums

Show a list of the latest albums from your favorite artists

### Model

- Genre
    - id
    - name

- Image
    - id
    - url
    - heightPx
    - widthPx

- Artist
    - id
    - name
    - uri
    - genres : List<Genre>
    - images: List<Image>

- Track
    - id
    - name
    - trackNumber
    - uri
    - duration
    - artists: List<Artist>

- Album
    - id
    - name
    - type
    - releaseDate
    - images: List<Image>
    - artists: List<Artist>
    - tracks: List<Track>

### Fetch Process

1. Daily at 6am
2. Get a User's Top Artists
    - GET https://api.spotify.com/v1/me/top/artists
3. For each artist, get each album
    - GET https://api.spotify.com/v1/artists/{id}/albums
4. For each album, get each track
    - GET https://api.spotify.com/v1/albums/{id}/tracks
5. Query for a list of new albums since the last update

### User Experience

1. Login Activity
2. Main Activity
    1. Bottom Navigation
        1. Fresh
        2. Singles
        3. Albums
    2. Fresh
        1. List 3 most recent singles
        2. Button: Show latest singles -> Latest Singles
        3. List 3 most recent albums
        4. Button: Show latest albums -> Latest Albums
    3. Latest Singles
        1. List 20 most recent singles
        2. Tap on a single -> Single Activity
    4. Latest Albums
        1. Show 20 most recent albums
        2. Tap on an album -> Album Activity
3. Single Activity
    1. Large image
    2. Song Title
    3. Artist name
    4. Release Date
    5. Genre
4. Album Activity
    1. Large image
    2. Artist name
    3. Release Date
    4. List of tracks
    5. Genre
5. Fresh Notification
    1. After fetch, show notification if new releases are found

### Architecture

1. LoginActivity
    1. LoginViewModel
        1. ->LoginRepository
            1. ->AccountService
            2. ->NetworkService
2. MainActivity
    1. BottomNavigationView
        1. FreshFragment
            1. FreshViewModel
                1. ->SingleRepository
                2. ->AlbumRepository
        2. LatestSinglesFragment
            1. LatestSinglesViewModel
                1. ->SingleRepository
        3. LatestAlbumsFragment
            1. LatestAlbumsViewModel
                1. ->AlbumRepository
3. SingleActivity
    1. SingleViewModel
        1. ->SingleRepository
4. AlbumActivity
    1. AlbumViewModel
        1. ->AlbumRepository
5. LoginRepository
    1. ->AccountService
    2. ->Cache
6. SingleRepository
7. AlbumRepository
8. Scheduled Job
    1. Bound foreground service, 6am daily
        1. ->Cache
        2. Notification
9. Cache
    1. UserIdentity
    2. FetchData
    4. SpotifyNetworkService
    5. RoomDatabase
