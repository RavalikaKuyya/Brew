package io.github.koss.brew.ui.thanks

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import io.github.koss.brew.keylineDivider
import io.github.koss.brew.sectionHeader
import io.github.koss.brew.textBlock
import io.github.koss.brew.ui.thanks.contributors.CodeContributors
import io.github.koss.brew.ui.thanks.contributors.DesignContributors
import io.github.koss.brew.ui.thanks.contributors.TestingContributors
import io.github.koss.brew.util.RalewayRegular
import io.github.koss.brew.util.extension.withModels
import kotlinx.android.synthetic.main.fragment_thanks.*

/**
 * Dialog fragment for displaying thanks for contributors to the Brew project
 */
class ThanksDialogFragment: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_thanks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply typeface to Collapsing layout
        val typeface = Typeface.createFromAsset(context?.assets, RalewayRegular.path)
        with(collapsingLayout) {
            setCollapsedTitleTypeface(typeface)
            setExpandedTitleTypeface(typeface)
        }

        // Add close button to dialog
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
        // NOTE - We have to use an empty menu to ensure the title collapses to the correct place
        toolbar.inflateMenu(R.menu.menu_empty)

        recyclerView.withModels {
            textBlock {
                id("thanks_opening_text_block")
                body(getString(R.string.thanks_header_description))
            }

            // Icon contributions
            sectionHeader {
                id("design_header")
                title(getString(R.string.design_contributions))
            }

            textBlock {
                id("icon_text_block")
                body(getString(R.string.design_thanks_description))
            }

            DesignContributors.contributors.map {
                it.generateBuildCallback(this@ThanksDialogFragment)
            }.forEach {
                it()
            }

            keylineDivider {
                id("icon_divider")
            }

            // Code contributions
            sectionHeader {
                id("code_contribution_header")
                title(getString(R.string.code_contributors))
            }

            textBlock {
                id("code_text_block")
                body(getString(R.string.code_thanks_description))
            }

            CodeContributors.contributors.map {
                it.generateBuildCallback(this@ThanksDialogFragment)
            }.forEach {
                it()
            }

            keylineDivider {
                id("code_divider")
            }

            // Testing contributors
            sectionHeader {
                id("test_contribution_header")
                title(getString(R.string.test_contributors))
            }

            textBlock {
                id("test_text_block")
                body(getString(R.string.test_thanks_description))
            }

            TestingContributors.contributors.map {
                it.generateBuildCallback(this@ThanksDialogFragment)
            }.forEach {
                it()
            }
        }
    }
}