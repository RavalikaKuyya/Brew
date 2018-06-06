package io.github.koss.brew.ui.you.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.BrewApp
import io.github.koss.brew.R
import io.github.koss.brew.ui.you.TitleProvider
import io.github.koss.brew.ui.you.settings.content.SettingsContent
import io.github.koss.brew.util.extension.withContent
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment: Fragment(), TitleProvider {

    @Inject
    lateinit var settingsContent: SettingsContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as? BrewApp)?.applicationComponent?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.withContent(settingsContent)
    }

    override fun getTitle(): String = "Settings"
}