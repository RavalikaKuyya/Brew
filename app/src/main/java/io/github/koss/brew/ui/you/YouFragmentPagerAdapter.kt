package io.github.koss.brew.ui.you

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.github.koss.brew.ui.you.profile.ProfileFragment
import io.github.koss.brew.ui.you.settings.SettingsFragment

/**
 * Fragment pager adapter for You tab
 */
class YouFragmentPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    private val fragments = listOf<Fragment>(ProfileFragment(), SettingsFragment())

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = (fragments[position] as? TitleProvider)?.getTitle()
}

/**
 * Interface for fragments to provide their own title
 */
interface TitleProvider {
    fun getTitle(): String
}