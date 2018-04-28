package io.github.koss.brew.ui.you

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import kotlinx.android.synthetic.main.fragment_you.*

class YouFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_you, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = YouFragmentPagerAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }
}