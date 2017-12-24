package io.github.jamiesanson.brew.ui.main.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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
class MainFragment: NestedScrollListener, Fragment() {

    @Inject
    lateinit var viewModelFactory: BrewViewModelFactory

    @Inject
    lateinit var ciceroneCache: LocalCiceroneCache
    lateinit var cicerone: Cicerone<Router>

    private lateinit var viewModel: MainFragmentViewModel

    private lateinit var homeTab: BottomTab
    private lateinit var discoverTab: BottomTab
    private lateinit var profileTab: BottomTab

    private var lastSelected = ""
    private var isAnimating = false

    private val bottomNavTabs
        get() = arrayOf(homeTab, discoverTab, profileTab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        cicerone = ciceroneCache.getCicerone(MAIN_FRAGMENT_TAG)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(MainFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseFragments()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            item: MenuItem ->

            val tabSelected = bottomNavTabs.first { it.menuId == item.itemId }
            viewModel.updateCurrentScreen(tabSelected.tag)

            return@setOnNavigationItemSelectedListener true
        }

        viewModel.currentScreen.observe(this, Observer { screenName ->
            if (lastSelected != screenName) {
                bottomNavigationView.selectedItemId = bottomNavTabs.first { screenName == it.tag }.menuId
                cicerone.router.replaceScreen(screenName)
                lastSelected = screenName ?: ""
            }
        })
    }

    override fun onScroll(direction: Direction) {
        if (!isAnimating) {
            val finalTranslation = when (direction) {
                Direction.UP -> 0f
                Direction.DOWN -> bottomNavigationView.height.toFloat()
            }

            bottomNavigationView
                    .animate()
                    .translationY(finalTranslation)
                    .setDuration(200L)
                    .withStartAction { isAnimating = true }
                    .withEndAction { isAnimating = false }
                    .start()
        }
    }

    private fun initialiseFragments() {
        val manager = childFragmentManager

        homeTab = BottomTab(
                fragment = manager.findFragmentByTag(HOME) ?: HomeFragment(),
                tag = HOME,
                menuId = R.id.action_home)

        discoverTab = BottomTab(
                fragment = manager.findFragmentByTag(DISCOVER) ?: DiscoverFragment(),
                tag = DISCOVER,
                menuId = R.id.action_discover)

        profileTab = BottomTab(
                fragment = manager.findFragmentByTag(PROFILE) ?: ProfileFragment(),
                tag = PROFILE,
                menuId = R.id.action_profile)

        bottomNavTabs.map {
            it.addToContainer(R.id.fragmentContainer, manager)
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
                    val manager = childFragmentManager
                    manager.switchTo(command.screenKey)
                }
                else -> throw IllegalStateException("Command not supported")
            }
        }
    }

    private fun FragmentManager.switchTo(tag: String) {
        val transaction = beginTransaction()

        bottomNavTabs
                .forEach {
                    if (it.tag == tag) {
                        if (!it.fragment.isDetached) {
                            transaction.detach(it.fragment)
                        }

                        transaction.attach(it.fragment)
                    } else {
                        transaction.detach(it.fragment)
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