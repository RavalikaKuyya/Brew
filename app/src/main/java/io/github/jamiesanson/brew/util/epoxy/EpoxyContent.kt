package io.github.jamiesanson.brew.util.epoxy

import android.arch.lifecycle.ViewModel
import android.content.Context

/**
 * Abstract class to be extended by classes providing content.
 * Can optionally be instantiated with a ViewModel backing
 */
abstract class EpoxyContent<VM: ViewModel> {

    lateinit var viewModel: VM
        private set

    fun castAndSetViewModel(viewModel: ViewModel) {
        @Suppress("UNCHECKED_CAST")
        this.viewModel = viewModel as VM
    }

    // To be overridden in the case of ViewModel instantiation
    open val viewModelClass: Class<VM>? = null

    abstract fun generateBuildCallback(context: Context): BuildCallback

    // Optional sub-content
    open val subcontent: List<EpoxyContent<out ViewModel>> = emptyList()
}