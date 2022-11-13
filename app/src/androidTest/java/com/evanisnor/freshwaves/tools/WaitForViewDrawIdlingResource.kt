package com.evanisnor.freshwaves.tools

import android.view.View
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController

class WaitForViewDrawIdlingResource : IdlingResource {

  private lateinit var callback: IdlingResource.ResourceCallback

  private var isDrawn = false

  fun wait(uiController: UiController, view: View) {
    IdlingRegistry.getInstance().register(this)
    view.viewTreeObserver.addOnDrawListener {
      isDrawn = true
      callback.onTransitionToIdle()
    }
    uiController.loopMainThreadUntilIdle()
    IdlingRegistry.getInstance().unregister(this)
  }

  override fun getName(): String = "Waiting for view to be drawn"

  override fun isIdleNow(): Boolean = isDrawn

  override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
    this.callback = callback
  }
}