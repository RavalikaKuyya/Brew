package io.github.jamiesanson.brew.util.event

import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings

/**
 * Class encapsulating events to be handled by the parent activity
 */
sealed class UiEvent

class MoveToDrinkScreen(val settings: RevealAnimationSettings?): UiEvent()

class ExitDrinkScreen: UiEvent()