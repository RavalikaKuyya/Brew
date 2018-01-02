package io.github.jamiesanson.brew.ui.create.drink

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.addPhotoHeader
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.util.anim.CircularRevealUtil
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import io.github.jamiesanson.brew.util.anim.StatusBarAnimationSettings
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.ExitDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.extension.withModels
import io.github.jamiesanson.brew.util.nav.BackButtonListener
import kotlinx.android.synthetic.main.fragment_drink.*
import kotlinx.android.synthetic.main.fragment_drink.view.*
import javax.inject.Inject

class DrinkFragment : BackButtonListener, Fragment() {

    @Inject lateinit var eventBus: UiEventBus
    @Inject lateinit var viewModelFactory: BrewViewModelFactory
    private lateinit var viewModel: DrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(DrinkViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_drink, container, false)

        if (!viewModel.isViewRevealed && arguments?.containsKey(ARG_REVEAL_SETTINGS) == true) {
            showCircularReveal(view)
        } else {
            activity?.window?.statusBarColor = ContextCompat.getColor(context!!, R.color.colorAccentDark)
            view.addImageView.visibility = View.GONE
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drinkToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView.withModels {
            addPhotoHeader {
                id("photo header")
                clickListener { _: View ->
                    Log.d("DrinkFragment", "Clicked")
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        showCircularExit {
            eventBus.postEvent(ExitDrinkScreen())
            viewModel.isViewRevealed = false
        }

        return true
    }

    private fun showCircularReveal(view: View) {
        var revealSettings = arguments?.getParcelable(ARG_REVEAL_SETTINGS) as RevealAnimationSettings

        // Update RevealSettings to reflect required behavior
        revealSettings = revealSettings.copy(
                duration = 500L,
                targetView = view,
                backgroundView = view.revealScrim,
                statusBarAnimationSettings = StatusBarAnimationSettings(
                        startColor = activity?.window?.statusBarColor ?: 0,
                        endColor = ContextCompat.getColor(context!!, R.color.colorAccentDark),
                        window = activity?.window
                )
        )

        // Move add icon to appropriate location
        val drawableWidth = ContextCompat.getDrawable(context!!, R.drawable.ic_add_black_24dp)?.intrinsicWidth ?: 0
        view.addImageView.x = (revealSettings.centerX - drawableWidth / 2).toFloat()
        view.addImageView.y = (revealSettings.centerY - drawableWidth / 2).toFloat()
        view.addImageView.visibility = View.VISIBLE

        // Start fade-out of ImageView
        view.addImageView.animate()
                .alpha(0f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(500L)
                .start()

        CircularRevealUtil.startCircularRevealEnterAnimation(revealSettings) {
            viewModel.isViewRevealed = true
        }
    }

    private fun showCircularExit(onFinish: () -> Unit) {
        var revealSettings = arguments?.getParcelable(ARG_REVEAL_SETTINGS) as RevealAnimationSettings

        // Update RevealSettings to reflect required behavior
        revealSettings = revealSettings.copy(
                duration = 300L,
                targetView = view,
                backgroundView = revealScrim,
                statusBarAnimationSettings = StatusBarAnimationSettings(
                        startColor = activity?.window?.statusBarColor ?: 0,
                        endColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark),
                        window = activity?.window
                )
        )

        // Start fade-in of icon ImageView
        with(view) {
            addImageView.animate()
                    .alpha(1f)
                    .setDuration(300L)
                    .start()
        }

        CircularRevealUtil.startCircularRevealExitAnimation(revealSettings, onFinish)
    }

    companion object {
        const val ARG_REVEAL_SETTINGS = "reveal_settings"
    }
}