package io.github.jamiesanson.brew.ui.main.fragment

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

class BottomTab(
        val fragment: Fragment,
        val tag: String,
        @IdRes val menuId: Int
) {

    fun addToContainer(@IdRes id: Int, manager: FragmentManager) {
        if (manager.findFragmentByTag(tag) == null) {
            manager.beginTransaction()
                    .add(id, fragment, tag)
                    .detach(fragment)
                    .commit()
        }
    }
}