package io.github.koss.brew.ui.thanks.contributors

import android.support.v4.app.Fragment
import io.github.koss.brew.clickableCell
import io.github.koss.brew.util.epoxy.BuildCallback
import org.jetbrains.anko.support.v4.browse

/**
 * Definition of all code contributors. Ignore all the hardcoded strings
 */
object CodeContributors {
    val contributors: List<CodeContributor> = listOf(
            CodeContributor("Jamie Sanson (Lead Developer)", "https://github.com/jamiesanson")
    )
}

/**
 * Class for a single contributor, which also defines how to build an epoxy model for itself
 */
class CodeContributor(
        private val displayName: String,
        private val link: String? = null
) {

    fun generateBuildCallback(fragment: Fragment): BuildCallback = {
        clickableCell {
            id(displayName)
            name(displayName)
            onClick { _ ->
                link?.let { fragment.browse(link, newTask = true) }
            }
        }
    }
}