package io.github.koss.brew.ui.you.settings.content.profilesettings

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import io.github.koss.brew.*
import io.github.koss.brew.util.Session
import io.github.koss.brew.util.epoxy.BuildCallback
import io.github.koss.brew.util.epoxy.EpoxyContent
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton

class ProfileSettingsContent: EpoxyContent<ProfileSettingsViewModel>() {

    override val viewModelClass = ProfileSettingsViewModel::class.java

    override fun generateBuildCallback(context: Context): BuildCallback = {
        if (Session.isLoggedIn) {
            sectionHeader {
                id("profile_settings_header")
                title(context.getString(R.string.profile_settings))
            }

            // Log out
            clickableCell {
                id("log_out_setting")
                name(context.getString(R.string.log_out))
                onClick { _ ->
                    context.alert {
                        title = context.getString(R.string.are_you_sure)
                        message = context.getString(R.string.log_out_dialog_message)
                        positiveButton(context.getString(R.string.log_out)) {
                            FirebaseAuth.getInstance().signOut()
                            viewModel.onLoggedOut()
                        }
                        cancelButton { it.cancel() }
                        show()
                    }
                }
            }

            // Final divider
            keylineDivider {
                id("profile_divider")
            }
        }
    }
}