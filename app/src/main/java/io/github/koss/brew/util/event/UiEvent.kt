package io.github.koss.brew.util.event

import io.github.koss.brew.util.anim.RevealAnimationSettings
import ru.terrakok.cicerone.commands.Command

/**
 * Class encapsulating events to be handled by the parent activity
 */
sealed class UiEvent

class MoveToAddDrinkScreen(val settings: RevealAnimationSettings?): UiEvent()

class MoveToDrinkScreen(val command: Command): UiEvent()

class ExitAddDrinkScreen : UiEvent()

class ExitDrinkScreen: UiEvent()

class RebuildHomescreen: UiEvent()

class ViewAllClicked: UiEvent()