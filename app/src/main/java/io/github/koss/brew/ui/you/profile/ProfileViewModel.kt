package io.github.koss.brew.ui.you.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import io.github.koss.brew.ui.you.profile.model.ProfileActivity
import javax.inject.Inject

class ProfileViewModel @Inject constructor(

): ViewModel() {

    val activity: LiveData<List<ProfileActivity>> = MutableLiveData()


}