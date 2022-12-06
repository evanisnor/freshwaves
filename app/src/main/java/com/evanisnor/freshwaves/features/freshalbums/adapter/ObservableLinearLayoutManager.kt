package com.evanisnor.freshwaves.features.freshalbums.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ObservableLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

  private var whenLayoutCompletedAction: ObservableLinearLayoutManager.() -> Unit = {}
  fun whenLayoutCompleted(action: ObservableLinearLayoutManager.() -> Unit) {
    whenLayoutCompletedAction = action
  }

  override fun onLayoutCompleted(state: RecyclerView.State?) {
    super.onLayoutCompleted(state)
    whenLayoutCompletedAction()
  }
}
