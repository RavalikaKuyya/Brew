package io.github.koss.brew.ui.drink

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.koss.brew.BrewApp
import io.github.koss.brew.R
import io.github.koss.brew.fullScreenLoadingIndicator
import io.github.koss.brew.util.extension.observe
import io.github.koss.brew.util.extension.withModels
import kotlinx.android.synthetic.main.activity_drink.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import javax.inject.Inject

/**
 * Activity for displaying a single drink
 */
class DrinkActivity: AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: DrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink)
        (application as BrewApp).applicationComponent.inject(this)
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(DrinkViewModel::class.java)

        val drinkId = intent.extras.getString(ARG_DRINK_ID) ?: throw IllegalArgumentException("Drink ID is required")

        viewModel.initialise(drinkId)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.state.observe(this) {
            it?.let { onStateChanged(it) }
        }
    }

    private fun onStateChanged(state: DrinkState) {
        when (state) {
            DrinkState.Loading -> showLoading()
            is DrinkState.DrinkRetrieved -> showDrinkRetrieved(state)
            is DrinkState.UnrecoverableError -> showUnrecoverableError(state.title, state.message)
        }
    }

    private fun showDrinkRetrieved(state: DrinkState.DrinkRetrieved) {
        if (state.drink == null) {
            showLoading()
            return
        }
    }

    private fun showLoading() {
        recyclerView.withModels {
            fullScreenLoadingIndicator {
                id("Loading")
            }
        }
    }

    private fun showUnrecoverableError(title: String, message: String) {
        alert {
            this.title = title
            this.message = message
            okButton {}
            show()
        }

        finish()
    }

    companion object {
        const val ARG_DRINK_ID = "arg_drink_id"
    }
}