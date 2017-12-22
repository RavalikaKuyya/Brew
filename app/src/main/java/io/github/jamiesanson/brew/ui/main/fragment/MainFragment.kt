package io.github.jamiesanson.brew.ui.main.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.SystemMessage
import android.widget.Toast
import io.github.jamiesanson.brew.ui.discover.DiscoverFragment
import io.github.jamiesanson.brew.ui.home.HomeFragment
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.ui.profile.ProfileFragment
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.nav.LocalCiceroneCache
import kotlinx.android.synthetic.main.fragment_main.*
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject

/**
 * This class is responsible for maintaining the main screens of the application,
 * those being the three screens accessible through bottom navigation
 */
class MainFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: BrewViewModelFactory

    @Inject
    lateinit var ciceroneCache: LocalCiceroneCache
    lateinit var cicerone: Cicerone<Router>

    private lateinit var viewModel: MainFragmentViewModel

    private lateinit var homeFragment: HomeFragment
    private lateinit var discoverFragment: DiscoverFragment
    private lateinit var profileFragment: ProfileFragment
    private val bottomNavFragments
        get() = arrayOf(homeFragment, discoverFragment, profileFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        cicerone = ciceroneCache.getCicerone(MAIN_FRAGMENT_TAG)
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MainFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentScreen.observe(this, Observer {
            cicerone.router.replaceScreen(it)
        })

        bottomNavigationView.setOnNavigationItemSelectedListener {
            item: MenuItem ->

            when (item.itemId) {
                R.id.action_home -> viewModel.setCurrentScreen(HOME)
                R.id.action_discover -> viewModel.setCurrentScreen(DISCOVER)
                R.id.action_profile -> viewModel.setCurrentScreen(PROFILE)
                else -> throw IllegalStateException("Action not supported")
            }

            return@setOnNavigationItemSelectedListener true
        }

        initialiseFragments()
    }

    private fun initialiseFragments() {
        val manager = activity?.supportFragmentManager ?: return

        if (manager.findFragmentByTag(HOME) == null) {
            homeFragment = HomeFragment()
            addFragment(homeFragment, HOME)
        } else {
            homeFragment = manager.findFragmentByTag(HOME) as HomeFragment
        }

        if (manager.findFragmentByTag(PROFILE) == null) {
            profileFragment = ProfileFragment()
            addFragment(profileFragment, PROFILE)
        } else {
            profileFragment = manager.findFragmentByTag(PROFILE) as ProfileFragment
        }

        if (manager.findFragmentByTag(DISCOVER) == null) {
            discoverFragment = DiscoverFragment()
            addFragment(discoverFragment, DISCOVER)
        } else {
            discoverFragment = manager.findFragmentByTag(DISCOVER) as DiscoverFragment
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val manager = activity?.supportFragmentManager ?: return

        if (manager.findFragmentByTag(tag) == null) {
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment, tag)
                    .detach(fragment)
                    .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        cicerone.navigatorHolder.setNavigator(MainFragmentNavigator())
    }

    override fun onPause() {
        super.onPause()
        cicerone.navigatorHolder.removeNavigator()
    }

    /**
     * Navigator specific to the main fragment. Handles changing of screens via bottom nav interactions
     */
    inner class MainFragmentNavigator: Navigator {
        override fun applyCommand(command: Command?) {
            when (command) {
                is Back -> activity?.finish()
                is SystemMessage -> Toast.makeText(activity, command.message, Toast.LENGTH_SHORT).show()
                is Replace -> {
                    val manager = activity?.supportFragmentManager ?: return
                    when (command.screenKey) {
                        BottomNavigationScreens.HOME -> manager.switchTo(homeFragment)
                        BottomNavigationScreens.DISCOVER -> manager.switchTo(discoverFragment)
                        BottomNavigationScreens.PROFILE -> manager.switchTo(profileFragment)
                    }
                }
                else -> throw IllegalStateException("Command not supported")
            }
        }
    }

    private fun FragmentManager.switchTo(fragment: Fragment) {
        val transaction = beginTransaction()

        for (item in bottomNavFragments) {
            if (item.tag == fragment.tag) {
                transaction.attach(fragment)
            } else {
                transaction.detach(item)
            }
        }

        transaction.commit()
    }

    companion object BottomNavigationScreens {
        const val HOME = "bottom_nav_home_screen"
        const val DISCOVER = "bottom_nav_discover_screen"
        const val PROFILE = "bottom_nav_profile_screen"

        const val MAIN_FRAGMENT_TAG = "main_frag_tag"
    }
}