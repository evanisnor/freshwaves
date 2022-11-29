package com.evanisnor.freshwaves.system

import android.app.Activity

/**
 * Stub interface for supporting the Debug menu. This keeps all other Debug references out of the
 * main and release source sets.
 */
interface DebugMenu {
  fun <T : Activity> onMenuItemClick(activity: T?, id: Int): Boolean = false
}
