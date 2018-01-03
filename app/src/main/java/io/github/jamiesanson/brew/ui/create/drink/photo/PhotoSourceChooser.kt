package io.github.jamiesanson.brew.ui.create.drink.photo

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.github.jamiesanson.brew.R
import kotlinx.android.synthetic.main.view_photo_source.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * BottomSheetDialog for choosing a photo source
 */
class PhotoSourceChooser: BottomSheetDialogFragment() {

    private var sources: List<PhotoSource> = emptyList()
    private var onSourceChosen: ((PhotoSource) -> Unit)? = null

    fun sources(source: PhotoSource, vararg sources: PhotoSource): PhotoSourceChooser {
        this.sources = listOf(source, *sources)
        return this
    }

    fun onSourceChosen(callback: (PhotoSource) -> Unit): PhotoSourceChooser {
        onSourceChosen = callback
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_chooser, container, false) as LinearLayout

        for (source in sources) {
            val sourceView = inflater.inflate(R.layout.view_photo_source, view, false)
            sourceView.iconImageView.setImageResource(source.iconRes)
            sourceView.titleTextView.setText(source.titleRes)
            sourceView.onClick {
                onSourceChosen?.invoke(source)
                dismiss()
            }
            view.addView(sourceView)
        }

        return view
    }
}