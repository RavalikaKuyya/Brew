package io.github.koss.brew.util.event

/**
 * Class encapsulating events to be handled by the parent activity
 */
sealed class UiEvent

class ExitAddDrinkScreen : UiEvent()

class RebuildHomescreen: UiEvent()

class ViewAllClicked: UiEvent()