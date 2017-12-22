package io.github.jamiesanson.brew.ui.main.navigator

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.github.jamiesanson.brew.ui.main.MainFragment
import ru.terrakok.cicerone.android.SupportFragmentNavigator

class BrewNavigator(private val fragmentManager: FragmentManager, containerId: Int): SupportFragmentNavigator(fragmentManager, containerId) {
    override fun createFragment(screenKey: String?, data: Any?): Fragment = when (screenKey) {
        Screens.MAIN_SCREEN -> MainFragment()
        else -> throw IllegalArgumentException("Screen key doesn't map to fragment ($screenKey)")
    }

    override fun exit() {
        return
    }

    override fun showSystemMessage(message: String?) {
        val view = fragmentManager.fragments[0].view
        if (view != null && message != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        }
    }
}