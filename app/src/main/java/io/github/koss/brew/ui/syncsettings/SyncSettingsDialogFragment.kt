package io.github.koss.brew.ui.syncsettings

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.*
import io.github.koss.brew.repository.config.ConfigurationWrapper
import io.github.koss.brew.repository.drinks.DrinkRepository
import io.github.koss.brew.util.RalewayRegular
import io.github.koss.brew.util.extension.withModels
import kotlinx.android.synthetic.main.fragment_sync_settings.*
import javax.inject.Inject

class SyncSettingsDialogFragment : DialogFragment() {

    private val config = ConfigurationWrapper(onLoaded = {
        try {
            recyclerView.requestModelBuild()
        } catch (e: Exception) { /* no-op */ }
    })

    @Inject
    lateinit var drinkRepository: DrinkRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)

        (requireActivity().application as BrewApp).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sync_settings, container, false)

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
                id("description_opening_block")
                body(getString(R.string.sync_settings_header))
            }

            sectionHeader {
                id("content_header")
                title(getString(R.string.content_settings))
            }

            textBlock {
                id("content_settings_description")
                body(getString(R.string.content_settings_header))
            }

            checkableItem {
                id("should_sync_content")
                title(getString(R.string.sync_your_drinks))
                value(config.shouldSyncDrinks)
                onCheckChanged { _, isChecked ->
                    config.shouldSyncDrinks = isChecked
                }
            }

            checkableItem {
                id("public_by_default")
                title(getString(R.string.public_by_default))
                value(config.publicByDefault)
                onCheckChanged { _, isChecked ->
                    config.publicByDefault = isChecked
                }
            }

            sectionHeader {
                id("timing_header")
                title(getString(R.string.timing_settings))
            }

            textBlock {
                id("timing_settings_description")
                body(getString(R.string.timing_setting_description))
            }

            checkableItem {
                id("cellular_sync")
                title(getString(R.string.sync_over_4g))
                value(config.syncOver4G)
                onCheckChanged { _, isChecked ->
                    config.syncOver4G = isChecked
                }
            }

            checkableItem {
                id("sync_immediately")
                title(getString(R.string.sync_immediately))
                value(config.shouldSyncImmediately)
                onCheckChanged { _, isChecked ->
                    config.shouldSyncImmediately = isChecked
                }
            }

            clickableCell {
                id("sync_now")
                name(getString(R.string.sync_now))
                onClick { _ ->
                    drinkRepository.syncLocalDrinks()
                }
            }
        }
    }
}
