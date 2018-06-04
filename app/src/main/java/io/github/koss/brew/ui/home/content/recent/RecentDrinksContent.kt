package io.github.koss.brew.ui.home.content.recent

import android.content.Context
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.github.koss.brew.*
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.util.epoxy.BuildCallback
import io.github.koss.brew.util.epoxy.EpoxyContent
import io.github.koss.brew.ui.drink.DrinkActivity
import kotlinx.android.synthetic.main.view_holder_drink_item.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick


class RecentDrinksContent : EpoxyContent<RecentDrinksViewModel>() {

    override val viewModelClass = RecentDrinksViewModel::class.java

    override fun generateBuildCallback(context: Context): BuildCallback = {
        val drinks = viewModel.recentDrinks.value ?: emptyList()

        if (drinks.isNotEmpty()) {
            carouselTitle {
                id(TITLE_ID)
                title(context.getString(R.string.your_recent_drinks))
            }

            carousel {
                id(CAROUSEL_ID)
                hasFixedSize(true)
                models(
                        ArrayList<DataBindingEpoxyModel>(drinks.take(RecentDrinksViewModel.RECENT_COUNT).map { drink ->
                            DrinkItem().apply {
                                id(drink.id)
                                title(drink.name)
                                tagsDisplay(drink.tags.take(3).joinToString(", ") { it.capitalize() })
                                photo(context, drink)
                                onLongClick { _ ->
                                    context.alert {
                                        message = "Delete ${drink.name}?"
                                        okButton {
                                            viewModel.removeDrink(drink)
                                        }
                                        cancelButton { }
                                        show()
                                    }
                                    true
                                }
                            }
                        })
                )
            }
        } else if (viewModel.shouldShowEmptyState()) {
            recentDrinksEmptyState {
                id("recent_drink_empty_state")
            }
        }
    }

    private fun DrinkItemBindingModelBuilder.photo(context: Context, drink: Drink) {
        this.onBind { _, view, _ ->
            view.dataBinding.root.onClick {
                it ?: return@onClick
                context.startActivity<DrinkActivity>(
                        DrinkActivity.ARG_DRINK_ID to drink.id.toString()
                )
            }

            val transform = MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(context.dip(6))
            )

            Glide.with(context)
                    .load(drink.photoUri)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.bitmapTransform(transform))
                    .into(view.dataBinding.root.backgroundImageView)
        }
    }

    /**
     * Custom epoxy carousel DSL
     */
    private inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
        CarouselModel_().apply {
            modelInitializer()
        }.addTo(this)
    }

    /**
     * Override of drink item model to enable state saving
     */
    internal class DrinkItem : DrinkItemBindingModel_() {
        override fun shouldSaveViewState(): Boolean = true
    }

    companion object {
        private const val TAG = "RecentDrinksContent"
        const val TITLE_ID = TAG + "_title"
        const val CAROUSEL_ID = TAG + "_carousel"
    }
}