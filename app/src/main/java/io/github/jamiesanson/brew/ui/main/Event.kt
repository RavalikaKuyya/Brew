package io.github.jamiesanson.brew.ui.main

/**
 * Class encapsulating the events that the MainViewModel must handle. These
 * are typically events which require new Fragments to be visible.
 */
sealed class Event {

    class AddDrink: Event()

    data class RemoveDrink(val id: Int): Event()
}