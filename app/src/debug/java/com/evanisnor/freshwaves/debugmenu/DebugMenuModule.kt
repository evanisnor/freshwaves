package com.evanisnor.freshwaves.debugmenu

import android.app.Activity
import android.content.Intent
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.system.DebugMenu
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DebugMenuModule {

  @Provides
  fun debugMenu(): DebugMenu = object : DebugMenu {
    override fun <T : Activity> onMenuItemClick(activity: T?, id: Int): Boolean =
      if (id == R.id.debug_menu_item && activity != null) {
        activity.startActivity(Intent(activity, DebugMenuActivity::class.java))
        true
      } else {
        false
      }
  }
}
