package io.github.koss.brew.ui.you.settings.content

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.ui.you.settings.content.appsettings.AppSettingsContent
import io.github.koss.brew.ui.you.settings.content.profilesettings.ProfileSettingsContent
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.epoxy.EpoxyContent
import io.github.koss.brew.util.epoxy.EpoxyContentProvider

class SettingsContent(viewModelFactory: BrewViewModelFactory): EpoxyContentProvider(viewModelFactory) {
    override val content: List<EpoxyContent<out ViewModel>>
        get() = listOf(
                AppSettingsContent(),
                DividerContent(id = "app_settings_divider"),
                ProfileSettingsContent())

}