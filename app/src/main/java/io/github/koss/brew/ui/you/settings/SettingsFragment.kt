package io.github.koss.brew.ui.you.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import io.github.koss.brew.ui.you.TitleProvider

class SettingsFragment: Fragment(), TitleProvider {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    override fun getTitle(): String = "Settings"
}