package io.github.koss.brew.ui.drink

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import io.github.koss.brew.R
import kotlinx.android.synthetic.main.layout_chip.view.*

/**
 * Simple class defining a chip, using ViewOutlineProvider to provide roundness
 */
class Chip @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_chip, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        with (chipTextView) {
            val viewOutlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, view.height.toFloat() / 2)
                }
            }
            outlineProvider = viewOutlineProvider
            clipToOutline = true
            isClickable = true
            isFocusable = true
            stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.chip_state_list_animator)
        }
    }

    fun setText(text: String) {
        chipTextView.text = text
    }
}