package io.github.jamiesanson.brew.ui.main.navigator

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.github.jamiesanson.brew.ui.create.drink.CreateDrinkFragment
import io.github.jamiesanson.brew.ui.main.fragment.MainFragment
import io.github.jamiesanson.brew.util.anim.CircularRevealUtil
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import ru.terrakok.cicerone.android.SupportFragmentNavigator

class BrewNavigator(private val fragmentManager: FragmentManager, containerId: Int): SupportFragmentNavigator(fragmentManager, containerId) {
    override fun createFragment(screenKey: String?, data: Any?): Fragment = when (screenKey) {
        Screens.MAIN_SCREEN -> MainFragment()
        Screens.ADD_DRINK_SCREEN -> {
            val fragment = CreateDrinkFragment()
            if (data != null) {
                val args = Bundle()
                args.putParcelable(CircularRevealUtil.ARG_REVEAL_SETTINGS, data as RevealAnimationSettings)
                fragment.arguments = args
            }
            fragment
        }
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