package io.github.jamiesanson.brew.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.jamiesanson.brew.R

/**
 * This class is responsible for maintaining the main screens of the application,
 * those being the three screens accessible through bottom navigation
 */
class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)
}