package io.github.koss.brew.ui.drink

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.util.event.UiEventBus
import javax.inject.Inject

class DrinkViewModel @Inject constructor(
        val uiEventBus: UiEventBus
): ViewModel() {

}