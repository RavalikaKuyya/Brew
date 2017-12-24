package io.github.jamiesanson.brew.ui.create.drink

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.util.anim.CircularRevealUtil
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.ExitDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.nav.BackButtonListener
import kotlinx.android.synthetic.main.fragment_drink.*
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

        if (!viewModel.isViewRevealed) {
            CircularRevealUtil.registerAddDrinkRevealEnterAnimation(
                    context = context!!,
                    view = view!!,
                    revealSettings = arguments?.getParcelable(CircularRevealUtil.ARG_REVEAL_SETTINGS) as RevealAnimationSettings,
                    listener = {
                        viewModel.isViewRevealed = true
                    }
            )
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drinkToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed(): Boolean {
        CircularRevealUtil.registerAddDrinkRevealExitAnimation(
                context = context!!,
                view = view!!,
                revealSettings = arguments?.getParcelable(CircularRevealUtil.ARG_REVEAL_SETTINGS) as RevealAnimationSettings,
                listener = {
                    eventBus.postEvent(ExitDrinkScreen())
                    viewModel.isViewRevealed = false
                }
        )

        return true
    }
}