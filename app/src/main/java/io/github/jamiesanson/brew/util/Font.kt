package io.github.jamiesanson.brew.util

/**
 * Class encapsulating all fonts used in Brew
 */
sealed class Font(val path: String)

class QuicksandRegular: Font("fonts/Quicksand-Regular.ttf")

class QuicksandMedium: Font("fonts/Quicksand-Medium.ttf")

class RalewayRegular: Font("fonts/Raleway-Regular.ttf")