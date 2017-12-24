package io.github.jamiesanson.brew.util.anim

import android.animation.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.ViewAnimationUtils
import android.os.Build
import android.annotation.TargetApi
import android.support.v4.content.ContextCompat
import android.support.annotation.ColorRes
import android.support.annotation.ColorInt

import android.content.Context

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.github.jamiesanson.brew.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay

object CircularRevealUtil {

    const val ARG_REVEAL_SETTINGS = "reveal_settings"

    @ColorInt
    private fun getColor(context: Context, @ColorRes colorId: Int): Int {
        return ContextCompat.getColor(context, colorId)
    }

    private fun registerCircularRevealAnimation(view: View, revealSettings: RevealAnimationSettings, startColor: Int, endColor: Int, listener: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    v.removeOnLayoutChangeListener(this)

                    val cx = revealSettings.centerX
                    val cy = revealSettings.centerY
                    val width = revealSettings.width
                    val height = revealSettings.height

                    //Simply use the diagonal of the view
                    val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
                    val anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius)
                    anim.duration = 500L
                    anim.interpolator = FastOutSlowInInterpolator()
                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            view.findViewById<View>(R.id.revealScrim).setBackgroundColor(startColor)
                            async(UI) {
                                delay((anim.duration * 0.4f).toLong())
                                startBackgroundColorAnimation(view.findViewById(R.id.revealScrim), startColor, endColor, 300)
                            }
                        }
                        override fun onAnimationEnd(animation: Animator) {
                            listener()
                        }
                    })
                    anim.start()
                }
            })
        } else {
            listener()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCircularRevealExitAnimation(view: View, revealSettings: RevealAnimationSettings, startColor: Int, endColor: Int, listener: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cx = revealSettings.centerX
            val cy = revealSettings.centerY
            val width = revealSettings.width
            val height = revealSettings.height

            val initRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f)
            anim.duration = 300L
            anim.interpolator = FastOutSlowInInterpolator()
            anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        startBackgroundColorAnimation(view.findViewById(R.id.revealScrim), startColor, endColor, 300)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        //Important: This will prevent the view's flashing (visible between the finished animation and the Fragment remove)
                        view.visibility = View.GONE
                        listener()
                    }
                }
            )
            anim.start()
        } else {
            listener()
        }
    }

    private fun startBackgroundColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.duration = duration.toLong()
        anim.interpolator = FastOutSlowInInterpolator()
        anim.addUpdateListener { valueAnimator -> view.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.start()
    }

    fun registerAddDrinkRevealEnterAnimation(context: Context, view: View, revealSettings: RevealAnimationSettings, listener: () -> Unit) {
        registerCircularRevealAnimation(view, revealSettings, revealSettings.startColor, getColor(context, android.R.color.transparent), listener)
    }

    fun registerAddDrinkRevealExitAnimation(context: Context, view: View, revealSettings: RevealAnimationSettings, listener: () -> Unit) {
        startCircularRevealExitAnimation(view, revealSettings, getColor(context, R.color.material_white), revealSettings.startColor, listener)
    }
}