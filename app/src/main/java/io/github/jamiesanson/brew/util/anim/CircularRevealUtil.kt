package io.github.jamiesanson.brew.util.anim

import android.animation.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.ViewAnimationUtils
import android.os.Build
import android.annotation.TargetApi

import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay

/**
 * Utility object for circularly revealing views easily. To be used on fragments when opened from a
 * FloatingActionButton interaction
 */
object CircularRevealUtil {

    fun startCircularRevealEnterAnimation(revealSettings: RevealAnimationSettings, listener: () -> Unit) {
        with (revealSettings) {
            targetView?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    v.removeOnLayoutChangeListener(this)

                    val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
                    val anim = ViewAnimationUtils.createCircularReveal(v, centerX, centerY, 0f, finalRadius)
                    with (anim) {
                        duration = revealSettings.duration
                        interpolator = FastOutSlowInInterpolator()
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator?) {
                                backgroundView?.setBackgroundColor(startColor)
                                async(UI) {
                                    delay((anim.duration * 0.4f).toLong())
                                    startBackgroundColorAnimation(backgroundView, startColor, endColor, 300)
                                }

                                if (statusBarAnimationSettings != null) {
                                    startStatusBarColorAnimation(statusBarAnimationSettings!!, revealSettings.duration)
                                }
                            }
                            override fun onAnimationEnd(animation: Animator) {
                                listener()
                            }
                        })
                        start()
                    }
                }
            })
        }
    }

    fun startCircularRevealExitAnimation(revealSettings: RevealAnimationSettings, listener: () -> Unit) {
        with (revealSettings) {
            val initRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(targetView, centerX, centerY, initRadius, 0f)
            anim.duration = revealSettings.duration
            anim.interpolator = FastOutSlowInInterpolator()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (backgroundView != null) {
                        startBackgroundColorAnimation(backgroundView, endColor, startColor, revealSettings.duration)

                        if (statusBarAnimationSettings != null) {
                            startStatusBarColorAnimation(statusBarAnimationSettings!!, revealSettings.duration)
                        }
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    //Important: This will prevent the view's flashing (visible between the finished animation and the Fragment remove)
                    targetView?.visibility = View.GONE
                    listener()
                }
            }
            )
            anim.start()
        }
    }

    private fun startBackgroundColorAnimation(view: View?, startColor: Int, endColor: Int, duration: Long) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.duration = duration
        anim.interpolator = FastOutSlowInInterpolator()
        anim.addUpdateListener { valueAnimator -> view?.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.start()
    }

    private fun startStatusBarColorAnimation(statusBarAnimationSettings: StatusBarAnimationSettings, dur: Long) {
        with (statusBarAnimationSettings) {
            with (ValueAnimator()) {
                setIntValues(startColor, endColor)
                setEvaluator(ArgbEvaluator())
                duration = dur
                interpolator = FastOutSlowInInterpolator()
                addUpdateListener { window?.statusBarColor = it.animatedValue as Int }
                start()
            }
        }
    }
}