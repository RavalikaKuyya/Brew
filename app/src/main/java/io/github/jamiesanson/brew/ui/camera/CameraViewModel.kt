package io.github.jamiesanson.brew.ui.camera

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import javax.inject.Inject

class CameraViewModel @Inject constructor(): ViewModel() {

    val state: LiveData<State> = MutableLiveData()

    private lateinit var uri: Uri

    init {
        state.observeForever {
            when (it) {
                is CameraViewModel.State.PhotoDeclined -> updateState(State.PreviewShowing())
                else -> {}
            }
        }
    }

    fun photoTaken(uri: Uri) {
        this.uri = uri
        updateState(State.PhotoTaken(uri))
    }

    fun photoAccepted() {
        updateState(State.PhotoAccepted(uri))
    }

    fun photoDeclined() {
        updateState(State.PhotoDeclined())
    }

    private fun updateState(state: State) {
        (this.state as MutableLiveData).postValue(state)
    }

    /**
     * Class encapsulating state of CameraActivity
     */
    sealed class State {
        class PreviewShowing: State()
        class PhotoTaken(val photoUri: Uri): State()
        class PhotoAccepted(val photoUri: Uri): State()
        class PhotoDeclined: State()
    }
}