package io.github.koss.brew.ui.you.settings.content.appsettings

import android.content.Context
import io.github.koss.brew.R
import io.github.koss.brew.clickableCell
import io.github.koss.brew.sectionHeader
import io.github.koss.brew.util.Session
import io.github.koss.brew.util.epoxy.BuildCallback
import io.github.koss.brew.util.epoxy.EpoxyContent
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton

class AppSettingsContent: EpoxyContent<AppSettingsViewModel>() {

    override val viewModelClass = AppSettingsViewModel::class.java

    override fun generateBuildCallback(context: Context): BuildCallback = {
        sectionHeader {
            id("app_settings_header")
            title(context.getString(R.string.app_settings))
        }

        // Clear local drinks
        clickableCell {
            id("clear_local")
            name(context.getString(R.string.clear_local_drinks))
            onClick { _ ->
                context.alert {
                    title = context.getString(R.string.are_you_sure)
                    message = if (Session.isLoggedIn)
                        context.getString(R.string.logged_in_local_delete_message) else
                        context.getString(R.string.logged_out_local_delete_message)
                    positiveButton(context.getString(R.string.button_continue)) {
                        viewModel.onAllLocalDrinksCleared()
                    }
                    cancelButton { it.cancel() }
                    show()
                }
            }
        }
    }
}