package io.github.koss.brew.ui.you

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import io.github.koss.brew.ui.you.loggedout.LoggedOutFragment
import io.github.koss.brew.ui.you.profile.ProfileFragment
import io.github.koss.brew.ui.you.settings.SettingsFragment

/**
 * Fragment pager adapter for You tab
 */
class YouFragmentPagerAdapter(fragmentManager: FragmentManager, private val isLoggedIn: () -> Boolean): FragmentPagerAdapter(fragmentManager) {

    private val fragments
        get() = listOf(
                if (isLoggedIn()) ProfileFragment() else LoggedOutFragment(),
                SettingsFragment())

    override fun getItem(position: Int): Fragment = fragments[position] as Fragment

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = (fragments[position] as? TitleProvider)?.getTitle()

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    override fun getItemId(position: Int): Long = fragments[position].hashCode().toLong()
}

/**
 * Interface for fragments to provide their own title
 */
interface TitleProvider {
    fun getTitle(): String
}