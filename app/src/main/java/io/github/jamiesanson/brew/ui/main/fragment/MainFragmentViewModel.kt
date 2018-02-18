package io.github.jamiesanson.brew.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.SparseArray
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