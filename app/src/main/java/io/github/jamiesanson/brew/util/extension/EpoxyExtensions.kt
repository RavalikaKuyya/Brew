package io.github.jamiesanson.brew.util.extension

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import io.github.jamiesanson.brew.util.epoxy.EpoxyContentProvider

/** Easily add models to an EpoxyRecyclerView, the same way you would in a buildModels method of EpoxyController. */
fun EpoxyRecyclerView.withModels(buildModelsCallback: EpoxyController.() -> Unit) {
    setControllerAndBuildModels(object : EpoxyController() {
        override fun buildModels() {
            buildModelsCallback()
        }
    })
}

fun EpoxyRecyclerView.withContent(contentProvider: EpoxyContentProvider) {
    setControllerAndBuildModels(object: EpoxyController() {
        override fun buildModels() {
            val callback = contentProvider.buildCallbackWith(context)
            callback()
        }
    })
}