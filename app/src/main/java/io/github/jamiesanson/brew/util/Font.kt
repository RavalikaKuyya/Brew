package io.github.jamiesanson.brew.util

/**
 * Class encapsulating all fonts used in Brew
 */
sealed class Font(val path: String)

class RobotoMonoRegular: Font("fonts/RobotoMono-Regular.ttf")

class QuicksandRegular: Font("fonts/Quicksand-Regular.ttf")

class RalewayRegular: Font("fonts/Raleway-Regular.ttf")