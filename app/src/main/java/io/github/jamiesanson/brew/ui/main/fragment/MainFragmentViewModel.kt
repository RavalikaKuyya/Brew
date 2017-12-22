package io.github.jamiesanson.brew.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(): ViewModel() {

    val currentScreen: LiveData<String> = MutableLiveData()

    fun setCurrentScreen(screen: String) {
        (currentScreen as MutableLiveData).postValue(screen)
    }
}