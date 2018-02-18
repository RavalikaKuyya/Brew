package io.github.koss.brew.util

/**
 * Class encapsulating all fonts used in Brew
 */
sealed class Font(val path: String)

object QuicksandRegular: Font("fonts/Quicksand-Regular.ttf")

object QuicksandMedium: Font("fonts/Quicksand-Medium.ttf")

object RalewayRegular: Font("fonts/Raleway-Regular.ttf")