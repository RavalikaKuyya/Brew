package io.github.koss.brew.ui.drink

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import io.github.koss.brew.*
import io.github.koss.brew.util.RalewayRegular
import io.github.koss.brew.util.extension.observe
import io.github.koss.brew.util.extension.withModels
import kotlinx.android.synthetic.main.activity_drink.*
import kotlinx.android.synthetic.main.view_holder_drink_tags.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
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
            drinkDetails {
                id(ID_DRINK_BODY)
                location("TODO - Make this part of the Drink Model")
                description(getString(R.string.lorem))
            }

            if (state.drink.tags.isNotEmpty()) {
                drinkTags {
                    id(ID_DRINK_TAGS)
                    tags(state.drink.tags)
                }
            }
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

    private fun DrinkTagsBindingModelBuilder.tags(tags: List<String>) {
        this.onBind { _, view, _ ->
            val recyclerView = view.dataBinding.root.tagsRecyclerView
            recyclerView.layoutManager = ChipsLayoutManager.newBuilder(this@DrinkActivity)
                    .build()

            recyclerView.withModels {
                tags.mapIndexed { index, tag ->
                    drinkTag {
                        id("$index $tag")
                        tag(tag)
                    }
                }
            }

        }
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