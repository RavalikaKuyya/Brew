package io.github.jamiesanson.brew.util.epoxy

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyController
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory

typealias BuildCallback = EpoxyController.() -> Unit

/**
 * Interface to be implemented by parent content groups, i.e HomeContentProvider etc.
 */
abstract class EpoxyContentProvider(
        private val viewModelFactory: BrewViewModelFactory
) {

    abstract val content: List<EpoxyContent<out ViewModel>>

    private lateinit var viewModelProvider: ViewModelProvider

    fun buildCallbackWith(context: Context): BuildCallback {
        this.viewModelProvider = ViewModelProviders.of(context as AppCompatActivity, viewModelFactory)

        val childCallbacks = ArrayList<BuildCallback>()
        content.mapTo(childCallbacks) { instantiateContent(context, it) }

        return {
            for (item in childCallbacks) {
                item()
            }
        }
    }

    private fun instantiateContent(context: Context, content: EpoxyContent<*>): BuildCallback {
        if (content.viewModelClass != null) {
            val viewModel = viewModelProvider.get(content.viewModelClass!!)
            content.castAndSetViewModel(viewModel)
        }

        val callback = content.generateBuildCallback(context)
        val childCallbacks = ArrayList<BuildCallback>()

        // Instantiate children
        content.subcontent.mapTo(childCallbacks) { instantiateContent(context, it) }

        return {
            callback()
            for (item in childCallbacks) {
                item()
            }
        }
    }
}