package io.github.jamiesanson.brew.ui.create.drink

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import javax.inject.Inject

class DrinkViewModel @Inject constructor(): ViewModel() {

    var isViewRevealed = false

    var photoUri: LiveData<Uri> = MutableLiveData()

    fun onPhotoAdded(uri: Uri) {
        (photoUri as MutableLiveData).postValue(uri)
    }
}