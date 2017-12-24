package io.github.jamiesanson.brew.ui.create.drink

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.util.anim.CircularRevealUtil
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings

class CreateDrinkFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_under_construction, container, false)
        CircularRevealUtil.registerAddDrinkRevealEnterAnimation(
                context = context!!,
                view = view!!,
                revealSettings = arguments?.getParcelable(CircularRevealUtil.ARG_REVEAL_SETTINGS) as RevealAnimationSettings
        )

        return view
    }
}