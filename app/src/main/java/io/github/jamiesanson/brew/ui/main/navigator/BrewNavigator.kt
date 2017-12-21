package io.github.jamiesanson.brew.ui.main.navigator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import ru.terrakok.cicerone.android.SupportFragmentNavigator

class BrewNavigator(fragmentManager: FragmentManager, containerId: Int): SupportFragmentNavigator(fragmentManager, containerId) {
    override fun createFragment(screenKey: String?, data: Any?): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSystemMessage(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}