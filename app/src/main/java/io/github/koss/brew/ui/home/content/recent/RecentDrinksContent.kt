package io.github.koss.brew.ui.home.content.recent

import android.content.Context
import android.support.annotation.DimenRes
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.github.koss.brew.*
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.util.epoxy.BuildCallback
import io.github.koss.brew.util.epoxy.EpoxyContent
import io.github.koss.brew.util.event.ViewAllClicked
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.view_holder_drink_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.app.Activity
import io.github.koss.brew.ui.drink.DrinkActivity
import org.jetbrains.anko.startActivity


class RecentDrinksContent(val asCarousel: Boolean): EpoxyContent<RecentDrinksViewModel>() {

    override val viewModelClass = RecentDrinksViewModel::class.java

    override fun generateBuildCallback(context: Context): BuildCallback = {
        val drinks = viewModel.recentDrinks.value ?: emptyList()

        if (drinks.isNotEmpty()) {
            if (asCarousel) {
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
                            }).apply {
                                add(ViewAllCarouselButtonBindingModel_().apply {
                                    id(VIEW_ALL_ID)
                                    onClick { _ ->
                                        viewModel.postEvent(ViewAllClicked())
                                    }
                                })
                            }
                    )
                }
            } else {
                carouselTitle {
                    id(TITLE_ID)
                    title(context.getString(R.string.your_recent_drinks))
                }

                for (drink in drinks) {
                    fullWidthDrinkItem {
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
                }
            }
        }
    }

    private fun DrinkItemBindingModelBuilder.photo(context: Context, drink: Drink) {
        @DimenRes
        fun Int.resolve(): Int = context.resources.getDimensionPixelSize(this)

        this.onBind { _, view, _ ->
            view.dataBinding.root.onClick {
                it ?: return@onClick
                context.startActivity<DrinkActivity>(
                        DrinkActivity.ARG_DRINK_ID to drink.id.toString()
                )
            }

            Glide.with(context)
                    .load(drink.photoUri)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(
                            BlurTransformation(),
                            CropTransformation(R.dimen.drink_item_width.resolve(), R.dimen.drink_item_height.resolve(), CropTransformation.CropType.CENTER))))
                    .into(view.dataBinding.root.backgroundImageView)
        }
    }

    private fun FullWidthDrinkItemBindingModelBuilder.photo(context: Context, drink: Drink) {
        @DimenRes
        fun Int.resolve(): Int = context.resources.getDimensionPixelSize(this)

        this.onBind { _, view, _ ->
            view.dataBinding.root.onClick {
                it ?: return@onClick
                context.startActivity<DrinkActivity>(
                        DrinkActivity.ARG_DRINK_ID to drink.id.toString()
                )
            }

            val imageView = view.dataBinding.root.backgroundImageView
            val metrics = android.util.DisplayMetrics()
            (context as Activity).windowManager
                    .defaultDisplay
                    .getMetrics(metrics)

            val margin = context.resources.getDimensionPixelSize(R.dimen.full_width_drink_margin) * 2

            Glide.with(context)
                    .load(drink.photoUri)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(
                            BlurTransformation(),
                            CropTransformation(metrics.widthPixels - margin, R.dimen.drink_item_height.resolve(), CropTransformation.CropType.CENTER))))
                    .into(imageView)
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
    internal class DrinkItem: DrinkItemBindingModel_() {
        override fun shouldSaveViewState(): Boolean = true
    }

    companion object {
        private const val TAG = "RecentDrinksContent"
        const val TITLE_ID = TAG + "_title"
        const val CAROUSEL_ID = TAG + "_carousel"
        const val VIEW_ALL_ID = TAG + "_view_all"
    }
}