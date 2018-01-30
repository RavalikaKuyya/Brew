package io.github.jamiesanson.brew.ui.home.content

import android.arch.lifecycle.ViewModel
import android.content.Context
import io.github.jamiesanson.brew.ui.home.content.recent.RecentDrinksContent
import io.github.jamiesanson.brew.util.epoxy.BuildCallback
import io.github.jamiesanson.brew.util.epoxy.EpoxyContent

class HomeContent : EpoxyContent<ViewModel>() {
    override fun generateBuildCallback(context: Context): BuildCallback = {}

    override val subcontent: List<EpoxyContent<out ViewModel>> =
        listOf(
            RecentDrinksContent()
        )
}