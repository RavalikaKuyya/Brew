package io.github.koss.brew.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.github.koss.brew.R
import android.graphics.Outline
import android.support.transition.AutoTransition
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.button_add_drink.view.*

/**
 * Floating action button containing "Add DrinkTable" text
 */
class AddDrinkFloatingActionButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private var animating: Boolean = false

    init {
        LayoutInflater.from(context).inflate(R.layout.button_add_drink, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val viewOutlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, view.height.toFloat() / 2)
            }
        }
        outlineProvider = viewOutlineProvider
        clipToOutline = true
        isClickable = true
        isFocusable = true
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.add_drink_state_list_animator)
    }

    fun expand(duration: Long) {
        if (!animating) {
            TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                setDuration(duration)
                addListener(CollapseListener())
                interpolator = AccelerateDecelerateInterpolator()
            })
            TransitionManager.beginDelayedTransition(constraintLayout, AutoTransition().apply {
                setDuration(duration)
                addListener(CollapseListener())
                interpolator = AccelerateDecelerateInterpolator()
            })

            addDrinkTextView.visibility = View.VISIBLE
        }
    }

    fun collapse(duration: Long) {
        if (!animating) {
            TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                setDuration(duration)
                addListener(CollapseListener())
                interpolator = AccelerateDecelerateInterpolator()
            })
            TransitionManager.beginDelayedTransition(constraintLayout, AutoTransition().apply {
                setDuration(duration)
                addListener(CollapseListener())
                interpolator = AccelerateDecelerateInterpolator()
            })

            addDrinkTextView.visibility = View.GONE
        }
    }

    inner class CollapseListener: Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            animating = false
            transition.removeListener(this)
        }

        override fun onTransitionResume(transition: Transition) {}

        override fun onTransitionPause(transition: Transition) {}

        override fun onTransitionCancel(transition: Transition) {
            animating = false
            transition.removeListener(this)
        }

        override fun onTransitionStart(transition: Transition) {
            animating = true
        }
    }

}

