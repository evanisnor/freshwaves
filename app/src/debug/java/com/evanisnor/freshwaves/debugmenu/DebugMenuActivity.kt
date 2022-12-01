package com.evanisnor.freshwaves.debugmenu

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanisnor.freshwaves.databinding.DebugActivityBinding
import com.evanisnor.freshwaves.debugmenu.items.UpdaterInformation
import com.evanisnor.freshwaves.features.notification.FreshAlbumNotifier
import com.evanisnor.freshwaves.features.updater.UpdaterBootstrapper
import com.evanisnor.freshwaves.spotify.cache.model.entities.Album
import com.evanisnor.freshwaves.spotify.cache.model.entities.Artist
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class DebugMenuActivity : AppCompatActivity() {

  @Inject
  lateinit var updaterBootstrapper: UpdaterBootstrapper

  @Inject
  lateinit var freshAlbumNotifier: FreshAlbumNotifier

  private lateinit var binding: DebugActivityBinding
  private lateinit var debugMenuAdapter: DebugMenuAdapter

  private val debugMenuViewModel: DebugMenuViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    debugMenuAdapter = DebugMenuAdapter()
    binding = DebugActivityBinding.inflate(layoutInflater).apply {
      setContentView(root)

      toolbar.setNavigationOnClickListener {
        finish()
      }

      debugMenu.apply {
        layoutManager = LinearLayoutManager(context)
        adapter = debugMenuAdapter
      }
    }

    lifecycleScope.launch {
      debugMenuAdapter.submit(debugMenuViewModel.appInformation())

      debugMenuViewModel.updaterStatus().collect { status ->
        debugMenuAdapter.submit(
          UpdaterInformation(
            state = status,
            lastRunState = debugMenuViewModel.updaterLastKnownState(),
            lastRunOn = debugMenuViewModel.updaterLastRunOn(),
            nextRunOn = debugMenuViewModel.updaterNextRunOn(),
            onUpdateNow = { updaterBootstrapper.updateNow() },
            onTestNotification = {
              launch {
                freshAlbumNotifier.send(testNotificationAlbums())
              }
            },
          ),
        )
      }
    }
  }

  private fun testNotificationAlbums() = listOf(
    Album(0, artist = Artist("A", "Artist A"), name = "First album of the year", releaseDate = Instant.now()),
    Album(1, artist = Artist("A", "Artist A"), name = "Second album of the year", releaseDate = Instant.now()),
    Album(2, artist = Artist("B", "Artist B"), name = "Album of the year", releaseDate = Instant.now()),
  )
}
