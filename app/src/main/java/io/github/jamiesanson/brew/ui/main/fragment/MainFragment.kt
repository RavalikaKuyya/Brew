package io.github.jamiesanson.brew.ui.main.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.SystemMessage
import android.widget.Toast
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.nav.LocalCiceroneCache
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject

/**
 * This class is responsible for maintaining the main screens of the application,
 * those being the three screens accessible through bottom navigation
 */
class MainFragment: Fragment() {

    @Inject
    lateinit var ciceroneCache: LocalCiceroneCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)

    /**
     * Navigator specific to the main fragment. Handles changing of screens via bottom nav interactions
     */
    inner class MainFragmentNavigator: Navigator {
        override fun applyCommand(command: Command?) {
            when (command) {
                is Back -> activity?.finish()
                is SystemMessage -> Toast.makeText(activity, command.message, Toast.LENGTH_SHORT).show()
                is Replace -> {
                    when (command.screenKey) {
                        BottomNavigationScreens.HOME -> {}
                        BottomNavigationScreens.DISCOVER -> {}
                        BottomNavigationScreens.PROFILE -> {}
                    }
                }
                else -> throw IllegalStateException("Command not supported")
            }
        }
    }

    companion object BottomNavigationScreens {
        const val HOME = "bottom_nav_home_screen"
        const val DISCOVER = "bottom_nav_discover_screen"
        const val PROFILE = "bottom_nav_profile_screen"

    }
}