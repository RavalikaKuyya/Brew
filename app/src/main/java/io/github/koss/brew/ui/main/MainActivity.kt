package io.github.koss.brew.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.koss.brew.R
import io.github.koss.brew.ui.main.navigator.BrewRouter
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.extension.component
import io.github.koss.brew.util.nav.BackButtonListener
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity: AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var viewModelFactory: BrewViewModelFactory

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var router: BrewRouter

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        component
                .plus(MainActivityModule(this))
                .inject(this)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MainViewModel::class.java)

        viewModel.init(router)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        var handled = false
        for (fragment in supportFragmentManager.fragments.filter { it is BackButtonListener }) {
            handled = handled || (fragment as BackButtonListener).onBackPressed()
        }

        if (!handled) {
            super.onBackPressed()
        }
    }
}