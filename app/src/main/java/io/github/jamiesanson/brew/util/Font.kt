package io.github.jamiesanson.brew.util

/**
 * Class encapsulating all fonts used in Brew
 */
sealed class Font(val path: String)

class RobotoMonoRegular: Font("fonts/RobotoMono-Regular.ttf")