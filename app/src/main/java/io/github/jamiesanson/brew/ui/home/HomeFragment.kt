package io.github.jamiesanson.brew.ui.home

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.ui.main.fragment.NestedScrollListener
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interceptScrollAndDispatchToParent()

    }

    /**
     * This method is a bit of a hack. For the BottomNavigationView in the MainFragment to hide on
     * scroll it needs to have access to callbacks fired by scrolling view layout behaviors. This
     * doesn't work in the current setup, as it isn't a direct child of the CoordinatorLayout.
     *
     * Note: NestedScroll events can be dispatched from *any* nested child, however a bug with
     * CollapsingToolbarLayout causes events to not be dispatched to nested or parent behaviors.
     */
    private fun interceptScrollAndDispatchToParent() {
        val params = scrollInterceptorView.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = ScrollInterceptionBehavior()
        behavior.scrollCallback {
            if (parentFragment is NestedScrollListener) {
                (parentFragment as NestedScrollListener).onScroll(it)
            }
        }
        params.behavior = behavior
        scrollInterceptorView.requestLayout()
    }
}