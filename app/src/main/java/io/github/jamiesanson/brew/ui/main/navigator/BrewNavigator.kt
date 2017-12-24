package io.github.jamiesanson.brew.ui.main.navigator

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.github.jamiesanson.brew.ui.create.drink.DrinkFragment
import io.github.jamiesanson.brew.ui.main.fragment.MainFragment
import io.github.jamiesanson.brew.util.anim.CircularRevealUtil
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

class BrewNavigator(
        private val fragmentManager: FragmentManager,
        private val containerId: Int
): SupportFragmentNavigator(fragmentManager, containerId) {

    override fun createFragment(screenKey: String?, data: Any?): Fragment = when (screenKey) {
        Screens.MAIN_SCREEN -> MainFragment()
        Screens.ADD_DRINK_SCREEN -> {
            val fragment = DrinkFragment()
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

    override fun applyCommand(command: Command?) {
        if (command is Forward && command.screenKey == Screens.ADD_DRINK_SCREEN) {
            val fragment = createFragment(command.screenKey, command.transitionData)

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction
                    .add(containerId, fragment, command.screenKey)
                    .commit()

        } else if (command is BackFromDrinkScreen) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction
                    .remove(fragmentManager.findFragmentByTag(Screens.ADD_DRINK_SCREEN))
                    .commit()
        } else {
            super.applyCommand(command)
        }
    }
}