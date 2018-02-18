package io.github.koss.brew.ui.home.content

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.ui.home.content.recent.RecentDrinksContent
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.epoxy.EpoxyContent
import io.github.koss.brew.util.epoxy.EpoxyContentProvider

class HomeContent(viewModelFactory: BrewViewModelFactory) : EpoxyContentProvider(viewModelFactory) {
    override val content: List<EpoxyContent<out ViewModel>>
        get() = contentList

    var contentList: List<EpoxyContent<out ViewModel>> = arrayListOf(
            RecentDrinksContent(asCarousel = true)
    )
}