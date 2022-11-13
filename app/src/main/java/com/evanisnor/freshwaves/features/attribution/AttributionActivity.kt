package com.evanisnor.freshwaves.features.attribution

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evanisnor.freshwaves.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttributionActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.attribution_activity)
  }
}
