package com.evanisnor.freshwaves.debugmenu

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanisnor.freshwaves.databinding.DebugActivityBinding
import com.evanisnor.freshwaves.debugmenu.items.UpdaterInformation
import com.evanisnor.freshwaves.features.updater.UpdaterBootstrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DebugMenuActivity : AppCompatActivity() {

    @Inject
    lateinit var updaterBootstrapper: UpdaterBootstrapper

    private lateinit var binding: DebugActivityBinding
    private lateinit var debugMenuAdapter: DebugMenuAdapter

    private val debugMenuViewModel: DebugMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        debugMenuAdapter = DebugMenuAdapter()
        binding = DebugActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)

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
                        onUpdateNow = { runUpdaterNow() }
                    )
                )
            }
        }
    }

    private fun runUpdaterNow() {
        updaterBootstrapper.updateNow(this)
    }
}