package com.evanisnor.freshwaves.tools

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController

class WaitForCallbackIdlingResource(private val description: String) : IdlingResource {

  private lateinit var callback: IdlingResource.ResourceCallback

  private var isDoneWaiting = false

  init {
    IdlingRegistry.getInstance().register(this)
  }

  fun wait(uiController: UiController) {
    try {
      uiController.loopMainThreadUntilIdle()
    } finally {
      IdlingRegistry.getInstance().unregister(this)
    }
  }

  fun proceed() {
    isDoneWaiting = true
    callback.onTransitionToIdle()
  }

  override fun getName(): String = description

  override fun isIdleNow(): Boolean = isDoneWaiting

  override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
    this.callback = callback
  }
}