package io.github.jamiesanson.brew.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.extension.component
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject

class MainActivity: AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var viewModelFactory: BrewViewModelFactory

    @Inject
    lateinit var navigator: Navigator

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component
                .plus(MainActivityModule(this))
                .inject(this)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MainViewModel::class.java)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}