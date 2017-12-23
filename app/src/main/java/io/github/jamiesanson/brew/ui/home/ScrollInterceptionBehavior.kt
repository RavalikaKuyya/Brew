package io.github.jamiesanson.brew.ui.home

import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.view.View
import android.widget.FrameLayout
import io.github.jamiesanson.brew.ui.main.fragment.Direction

class ScrollInterceptionBehavior : CoordinatorLayout.Behavior<View>() {

    private var onScroll: ((Direction) -> Unit)? = null

    fun scrollCallback(onScroll: (Direction) -> Unit) {
        this.onScroll = onScroll
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is FrameLayout
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy < 0) {
            onScroll?.invoke(Direction.UP)
        } else if (dy > 0) {
            onScroll?.invoke(Direction.DOWN)
        }
    }
}