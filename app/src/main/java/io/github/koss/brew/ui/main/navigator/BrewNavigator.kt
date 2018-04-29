package io.github.koss.brew.ui.main.navigator

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.github.koss.brew.ui.create.drink.AddDrinkFragment
import io.github.koss.brew.ui.create.drink.AddDrinkFragment.Companion.ARG_REVEAL_SETTINGS
import io.github.koss.brew.ui.main.fragment.MainFragment
import io.github.koss.brew.util.anim.RevealAnimationSettings
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Command

class BrewNavigator(
        private val fragmentManager: FragmentManager,
        private val containerId: Int
): SupportFragmentNavigator(fragmentManager, containerId) {

    override fun createFragment(screenKey: String?, data: Any?): Fragment = when (screenKey) {
        Screens.MAIN_SCREEN -> MainFragment()
        Screens.ADD_DRINK_SCREEN -> {
            val fragment = AddDrinkFragment()
            if (data != null) {
                val args = Bundle()
                args.putParcelable(ARG_REVEAL_SETTINGS, data as RevealAnimationSettings)
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
        if (command is Add) {
            if (fragmentManager.findFragmentByTag(command.screenkey) == null) {
                val fragment = createFragment(command.screenkey, command.transitionData)
                val fragmentTransaction = fragmentManager.beginTransaction()

                setupFragmentTransactionAnimation(
                        command,
                        fragmentManager.findFragmentById(containerId),
                        fragment,
                        fragmentTransaction
                )

                fragmentTransaction
                        .add(containerId, fragment, command.screenkey)
                        .commit()
            }
        } else if (command is Remove) {
            if (fragmentManager.findFragmentByTag(command.screenkey) != null) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = fragmentManager.findFragmentByTag(command.screenkey)

                setupFragmentTransactionAnimation(
                        command,
                        fragmentManager.findFragmentById(containerId),
                        fragment,
                        fragmentTransaction
                )

                fragmentTransaction
                        .remove(fragment)
                        .commit()
            }
        } else {
            super.applyCommand(command)
        }
    }
}