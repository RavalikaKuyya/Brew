package io.github.jamiesanson.brew.ui.main.navigator

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewCompat
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.ui.create.drink.AddDrinkFragment
import io.github.jamiesanson.brew.ui.create.drink.AddDrinkFragment.Companion.ARG_REVEAL_SETTINGS
import io.github.jamiesanson.brew.ui.drink.DrinkFragment
import io.github.jamiesanson.brew.ui.drink.DrinkFragment.Companion.ARG_DRINK
import io.github.jamiesanson.brew.ui.main.fragment.MainFragment
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import org.jetbrains.anko.bundleOf
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

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
        Screens.DRINK_SCREEN -> {
            val fragment = DrinkFragment()
            fragment.arguments = bundleOf(ARG_DRINK to data as Drink)
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

    override fun setupFragmentTransactionAnimation(command: Command?, currentFragment: Fragment?, nextFragment: Fragment?, fragmentTransaction: FragmentTransaction?) {
        if (command is ForwardToDrinkScreen && nextFragment is DrinkFragment) {
            fragmentTransaction?.addSharedElement(command.imageView, ViewCompat.getTransitionName(command.imageView))
        } else if (command is Back && currentFragment is DrinkFragment) {
            // TODO: Setup shared element
        } else {
            super.setupFragmentTransactionAnimation(command, currentFragment, nextFragment, fragmentTransaction)
        }
    }

    override fun applyCommand(command: Command?) {
        if (command is Forward && command.screenKey == Screens.ADD_DRINK_SCREEN) {
            if (fragmentManager.findFragmentByTag(Screens.ADD_DRINK_SCREEN) == null) {
                val fragment = createFragment(command.screenKey, command.transitionData)

                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction
                        .add(containerId, fragment, command.screenKey)
                        .commitNow()
            }
        } else if (command is BackFromAddDrinkScreen) {
            if (fragmentManager.findFragmentByTag(Screens.ADD_DRINK_SCREEN) != null) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction
                        .remove(fragmentManager.findFragmentByTag(Screens.ADD_DRINK_SCREEN))
                        .commitNow()
            }
        } else {
            super.applyCommand(command)
        }
    }
}