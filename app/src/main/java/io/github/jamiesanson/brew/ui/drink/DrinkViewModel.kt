package io.github.jamiesanson.brew.ui.drink

import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.util.event.UiEventBus
import javax.inject.Inject

class DrinkViewModel @Inject constructor(
        val uiEventBus: UiEventBus
): ViewModel() {

}