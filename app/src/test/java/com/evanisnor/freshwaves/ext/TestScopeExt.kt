package com.evanisnor.freshwaves.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

/**
 * Syntactically pleasant way ot launching a suspend function from an anonymous Fragment.
 */
fun TestScope.launchInFragment(launchOnFragment: suspend (Fragment) -> Unit) {
  with(launchFragment<Fragment>()) {
    onFragment {
      it.lifecycleScope.launch {
        launchOnFragment(it)
      }
    }
  }
}
