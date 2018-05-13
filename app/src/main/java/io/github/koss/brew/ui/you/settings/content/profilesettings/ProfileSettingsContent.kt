package io.github.koss.brew.ui.you.settings.content.profilesettings

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import io.github.koss.brew.R
import io.github.koss.brew.clickableSetting
import io.github.koss.brew.sectionHeader
import io.github.koss.brew.ui.main.MainActivity
import io.github.koss.brew.ui.main.fragment.MainFragment
import io.github.koss.brew.ui.main.navigator.Screens
import io.github.koss.brew.ui.you.YouFragment
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
            clickableSetting {
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
        }
    }
}