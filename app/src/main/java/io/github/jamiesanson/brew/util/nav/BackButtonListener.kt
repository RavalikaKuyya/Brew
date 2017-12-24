package io.github.jamiesanson.brew.util.nav

/**
 * Interface to be implemented by fragments to handle back presses
 */
interface BackButtonListener {
    fun onBackPressed(): Boolean
}