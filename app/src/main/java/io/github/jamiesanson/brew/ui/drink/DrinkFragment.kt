package io.github.jamiesanson.brew.ui.drink

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.transition.TransitionInflater
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.clickableTextAttribute
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.util.RalewayRegular
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.ExitDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.extension.withModels
import io.github.jamiesanson.brew.util.nav.BackButtonListener
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.fragment_drink.*
import javax.inject.Inject

class DrinkFragment: Fragment(), BackButtonListener {

    @Inject
    lateinit var viewModelFactory: BrewViewModelFactory

    @Inject
    lateinit var eventBus: UiEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_drink, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.containsKey(ARG_DRINK_REVEAL_SETTINGS) == false) throw IllegalArgumentException("DrinkFragment must be started with a drink")

        val revealSettings = arguments!![ARG_DRINK_REVEAL_SETTINGS] as DrinkRevealSettings
        val drink = revealSettings.drink

        showReveal(revealSettings)

        Glide.with(this)
                .load(drink.photoUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(MultiTransformation(
                        CropTransformation(drinkImageView.width, drinkImageView.height, CropTransformation.CropType.CENTER))))
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        setupCollapsingLayout(drink.name, ContextCompat.getColor(context!!, R.color.material_white))
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        setupCollapsingLayout(drink.name, ContextCompat.getColor(context!!, R.color.material_white))
                        return false
                    }

                })
                .into(drinkImageView)

        recyclerView.withModels {
            clickableTextAttribute {
                id(TAG_TAGS)
                title(getString(R.string.tags))
                value(drink.tags.joinToString(separator = ", "))
            }
        }
    }

    override fun onBackPressed(): Boolean {
        eventBus.postEvent(ExitDrinkScreen())
        return true
    }

    private fun setupCollapsingLayout(newTitle: String, colorInt: Int) {
        val typeface = Typeface.createFromAsset(context?.assets, RalewayRegular.path)
        with (collapsingLayout) {
            title = newTitle
            setExpandedTitleColor(colorInt)
            collapsingLayout.setCollapsedTitleTextColor(colorInt)
            setCollapsedTitleTypeface(typeface)
            setExpandedTitleTypeface(typeface)
        }
    }

    private fun showReveal(settings: DrinkRevealSettings) {
        var listener: ViewTreeObserver.OnPreDrawListener? = null

        listener = ViewTreeObserver.OnPreDrawListener {
            drinkImageView.viewTreeObserver.removeOnPreDrawListener(listener)

            with (drinkImageView) {
                val (_, thumbWidth, thumbHeight, thumbX, thumbY) = settings
                val location = IntArray(2)
                drinkImageView.getLocationOnScreen(location)
                val left = location[0]
                val top = location[1]

                val leftDelta = thumbX - left
                val topDelta = thumbY - top


                val widthScale = thumbWidth/width

                val heightScale = thumbHeight/height

                pivotX = 0F
                pivotY = 0F

                scaleX = widthScale.toFloat()
                scaleY = heightScale.toFloat()

                translationX = leftDelta.toFloat()
                translationY = topDelta.toFloat()


                animate()
                        .scaleX(1F)
                        .scaleY(1F)
                        .translationX(0F)
                        .translationY(0F)
                        .setInterpolator(DecelerateInterpolator())


            }
            true
        }

        drinkImageView.viewTreeObserver.addOnPreDrawListener(listener)
    }


    companion object {
        const val ARG_DRINK_REVEAL_SETTINGS = "arg_drink_reveal_settings"
        const val TAG_TAGS = "tags"
    }
}