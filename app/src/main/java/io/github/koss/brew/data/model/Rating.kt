package io.github.koss.brew.data.model

data class Rating(
        val score: Double,
        val review: String
) {
    companion object {
        const val SCORE_UNDEFINED = -1.0
    }
}