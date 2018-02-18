package io.github.jamiesanson.brew.ui.home.content

import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.ui.home.content.recent.RecentDrinksContent
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.epoxy.EpoxyContent
import io.github.jamiesanson.brew.util.epoxy.EpoxyContentProvider

class HomeContent(viewModelFactory: BrewViewModelFactory) : EpoxyContentProvider(viewModelFactory) {
    override val content: List<EpoxyContent<out ViewModel>>
        get() = contentList

    var contentList: List<EpoxyContent<out ViewModel>> = arrayListOf(
            RecentDrinksContent(asCarousel = true)
    )
}