package io.github.jamiesanson.brew.ui.home.content.recent

import android.content.Context
import android.support.annotation.DimenRes
import android.support.v4.view.ViewCompat
import android.util.Log
import android.widget.ImageView
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.github.jamiesanson.brew.*
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.ui.main.navigator.ForwardToDrinkScreen
import io.github.jamiesanson.brew.util.epoxy.BuildCallback
import io.github.jamiesanson.brew.util.epoxy.EpoxyContent
import io.github.jamiesanson.brew.util.event.MoveToDrinkScreen
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.view_holder_drink_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk25.coroutines.onClick

class RecentDrinksContent: EpoxyContent<RecentDrinksViewModel>() {

    override val viewModelClass = RecentDrinksViewModel::class.java

    override fun generateBuildCallback(context: Context): BuildCallback = {
        val drinks = viewModel.recentDrinks.value ?: emptyList()

        if (drinks.isNotEmpty()) {
            carouselTitle {
                id("drinks title")
                title(context.getString(R.string.your_recent_drinks))
            }

            carousel {
                id("carousel")
                models(
                        ArrayList<DataBindingEpoxyModel>(drinks.map { drink -> DrinkItemBindingModel_().apply {
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
                                    cancelButton {  }
                                    show()
                                }
                                true
                            }
                        }}).apply {
                            val added = add(ViewAllCarouselButtonBindingModel_().apply {
                                id("view all")
                                onClick { _ ->
                                    Log.d("HomeFragment", "View all clicked")
                                }
                            })
                        }
                )
            }
        }
    }

    private fun onDrinkClicked(sharedImageView: ImageView, drink: Drink) {
        viewModel.postEvent(MoveToDrinkScreen(
                command = ForwardToDrinkScreen(sharedImageView, drink)
        ))
    }

    private fun DrinkItemBindingModelBuilder.photo(context: Context, drink: Drink) {
        @DimenRes
        fun Int.resolve(): Int = context.resources.getDimensionPixelSize(this)

        this.onBind { _, view, _ ->
            view.dataBinding.root.onClick {
                it ?: return@onClick
                onDrinkClicked(it.backgroundImageView, drink)
            }

            ViewCompat.setTransitionName(view.dataBinding.root.backgroundImageView, drink.id.toString())

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


    /**
     * Custom epoxy carousel DSL
     */
    private inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
        CarouselModel_().apply {
            modelInitializer()
        }.addTo(this)
    }
}