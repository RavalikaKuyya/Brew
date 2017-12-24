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
import io.github.jamiesanson.brew.util.event.ExitDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.nav.BackButtonListener
import javax.inject.Inject

class CreateDrinkFragment: BackButtonListener, Fragment() {

    @Inject lateinit var eventBus: UiEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_under_construction, container, false)
        CircularRevealUtil.registerAddDrinkRevealEnterAnimation(
                context = context!!,
                view = view!!,
                revealSettings = arguments?.getParcelable(CircularRevealUtil.ARG_REVEAL_SETTINGS) as RevealAnimationSettings
        )

        return view
    }

    override fun onBackPressed(): Boolean {
        CircularRevealUtil.registerAddDrinkRevealExitAnimation(
                context = context!!,
                view = view!!,
                revealSettings = arguments?.getParcelable(CircularRevealUtil.ARG_REVEAL_SETTINGS) as RevealAnimationSettings,
                listener = {
                    eventBus.postEvent(ExitDrinkScreen())
                }
        )

        return true
    }
}