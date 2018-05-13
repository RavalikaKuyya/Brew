package io.github.koss.brew.ui.you.settings.content.about

import android.content.Context
import io.github.koss.brew.*
import io.github.koss.brew.ui.main.MainActivity
import io.github.koss.brew.ui.thanks.ThanksDialogFragment
import io.github.koss.brew.util.epoxy.BuildCallback
import io.github.koss.brew.util.epoxy.EpoxyContent

class AboutBrewContent: EpoxyContent<Nothing>() {
    override fun generateBuildCallback(context: Context): BuildCallback = {
        sectionHeader {
            id("about_section_header")
            title(context.getString(R.string.about_brew))
        }

        // Kudos
        clickableCell {
            id("about_brew_kudos")
            name(context.getString(R.string.who_helped_out))
            onClick { _ ->
                val fragment = ThanksDialogFragment()
                fragment.show((context as MainActivity).supportFragmentManager, THANKS_FRAGMENT_TAG)
            }
        }

        versionFooter {
            id("brew_version_footer")
            buildVersionText("Brew build ${BuildConfig.VERSION_NAME}/${BuildConfig.BUILD_TYPE}")
        }
    }

    companion object {
        const val THANKS_FRAGMENT_TAG = "thanks_fragment"
    }
}