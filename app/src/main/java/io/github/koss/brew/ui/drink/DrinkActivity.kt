package io.github.koss.brew.ui.drink

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import io.github.koss.brew.*
import io.github.koss.brew.util.RalewayRegular
import io.github.koss.brew.util.extension.observe
import io.github.koss.brew.util.extension.withModels
import kotlinx.android.synthetic.main.activity_drink.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import uk.co.chrisjenx.calligraphy.CalligraphyUtils
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
        backButton.onClick {
            finish()
        }

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

        // Set up header
        titleTextView.text = state.drink.name
        CalligraphyUtils.applyFontToTextView(this, titleTextView, RalewayRegular.path)

        // Set up drink image
        Glide.with(this)
                .load(state.drink.photoUri)
                .apply(centerCropTransform())
                .into(drinkImageView)

        recyclerView.withModels {
            // TODO - Add drink things here
        }
    }

    private fun showLoading() {
        recyclerView.withModels {
            fullScreenLoadingIndicator {
                id(ID_DRINK_LOADING)
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
        // Arguments
        const val ARG_DRINK_ID = "arg_drink_id"

        // Epoxy IDs
        const val ID_DRINK_LOADING = "loading"
        const val ID_DRINK_TITLE = "title"
        const val ID_DRINK_BODY = "drink_body"
        const val ID_DRINK_TAGS = "drink_tags"
        const val ID_DRINKS_RELATED = "related_drinks"
    }
}