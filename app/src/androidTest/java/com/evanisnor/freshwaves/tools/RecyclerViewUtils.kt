package com.evanisnor.freshwaves.tools

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class RecyclerViewUtils {

    companion object {

        fun scrollToPosition(position: Int): ViewAction = ScrollToPositionViewAction(position)

        fun atPositionOnView(position: Int, id: Int, matcher: Matcher<View>) =
            AtPositionOnViewMatcher(position, id, matcher)

        fun atPositionOnView(position: Int, action: ViewAction) =
            AtPositionOnViewAction(position, action)
    }

}

class ScrollToPositionViewAction(
    private val position: Int
) : ViewAction {

    override fun getDescription() = "Scroll RecyclerView to position $position"

    override fun getConstraints(): Matcher<View> = Matchers.allOf(
        ViewMatchers.isEnabled(),
        ViewMatchers.isDisplayed(),
        ViewMatchers.isAssignableFrom(RecyclerView::class.java)
    )

    override fun perform(uiController: UiController, view: View) {
        with(view as RecyclerView) {
            scrollToPosition(position)
        }
    }
}

class AtPositionOnViewMatcher(
    private val position: Int,
    private val id: Int,
    private val matcher: Matcher<View>
) : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("has $matcher at position $position")
    }

    override fun matchesSafely(recyclerView: RecyclerView): Boolean {
        return matcher.matches(
            recyclerView.findViewHolderForAdapterPosition(position)
                ?.itemView
                ?.findViewById(id)
        )
    }

}

class AtPositionOnViewAction(
    private val position: Int,
    private val action: ViewAction
) : ViewAction {

    override fun getDescription() = "perform action $action at position $position"

    override fun getConstraints(): Matcher<View> = Matchers.allOf(
        ViewMatchers.isEnabled(),
        ViewMatchers.isDisplayed(),
        ViewMatchers.isAssignableFrom(RecyclerView::class.java)
    )

    override fun perform(uiController: UiController, view: View) {
        with(view as RecyclerView) {
            val holder = view.findViewHolderForAdapterPosition(position)
            action.perform(uiController, holder?.itemView)
        }
    }
}