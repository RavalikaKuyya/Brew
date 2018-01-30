package io.github.jamiesanson.brew.util.epoxy

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.airbnb.epoxy.EpoxyController

typealias BuildCallback = EpoxyController.() -> Unit

class EpoxyContentFactory(
        private val viewModelProvider: ViewModelProvider
) {

    fun instantiate(context: Context, content: EpoxyContent<*>): BuildCallback {
        if (content.viewModelClass != null) {
            val viewModel = viewModelProvider.get(content.viewModelClass!!)
            content.castAndSetViewModel(viewModel)
        }

        val callback = content.generateBuildCallback(context)
        val childCallbacks = ArrayList<BuildCallback>()

        // Instantiate children
        content.subcontent.mapTo(childCallbacks) { instantiate(context, it) }

        return {
            callback()
            for (item in childCallbacks) {
                item()
            }
        }
    }
}