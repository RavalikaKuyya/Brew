package io.github.koss.brew.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(): ViewModel() {

    val currentScreen: LiveData<String> = MutableLiveData()

    val childState: HashMap<String, Bundle> = HashMap()

    init {
        updateCurrentScreen(MainFragment.BottomNavigationScreens.HOME)
    }

    fun updateCurrentScreen(newScreen: String) {
        if (currentScreen.value != newScreen) {
            (currentScreen as MutableLiveData).postValue(newScreen)
        }
    }
}