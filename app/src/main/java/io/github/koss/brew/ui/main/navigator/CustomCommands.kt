package io.github.koss.brew.ui.main.navigator

import ru.terrakok.cicerone.commands.Command

/**
 * Class encapsulating custom commands for Cicero
 */
sealed class CustomCommand: Command

open class Add(val screenkey: String, val transitionData: Any? = null): CustomCommand()

open class Remove(val screenkey: String, val transitionData: Any? = null): CustomCommand()