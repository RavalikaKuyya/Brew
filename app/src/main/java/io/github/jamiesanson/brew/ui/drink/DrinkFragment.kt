package io.github.jamiesanson.brew.ui.drink

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.DimenRes
import android.support.transition.TransitionInflater
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.util.RalewayRegular
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.fragment_drink.*

class DrinkFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater
                .from(context)
                .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_drink, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.containsKey(ARG_DRINK) == false) throw IllegalArgumentException("DrinkFragment must be started with a drink")

        val drink = arguments!![ARG_DRINK] as Drink
        val transitionName = drink.id.toString()
        drinkImageView.transitionName = transitionName
        Glide.with(this)
                .load(drink.photoUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(MultiTransformation(
                        CropTransformation(drinkImageView.width, drinkImageView.height, CropTransformation.CropType.CENTER))))
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        setupCollapsingLayout(drink.name, ContextCompat.getColor(context!!, R.color.material_white))
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        setupCollapsingLayout(drink.name, ContextCompat.getColor(context!!, R.color.material_white))
                        return false
                    }

                })
                .into(drinkImageView)
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


    companion object {
        const val ARG_DRINK = "arg_drink"
    }
}