package io.github.koss.brew.ui.you.settings.content.profilesettings

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.ui.you.OnRefreshYouFragmentRequested
import io.github.koss.brew.util.arch.EventBus
import javax.inject.Inject

class ProfileSettingsViewModel @Inject constructor(
    private val eventBus: EventBus
): ViewModel() {

    fun onLoggedOut() {
        eventBus.send(OnRefreshYouFragmentRequested)
    }
}