package com.evanisnor.freshwaves.debugmenu

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanisnor.freshwaves.databinding.DebugActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DebugMenuActivity : AppCompatActivity() {

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
        }

    }
}