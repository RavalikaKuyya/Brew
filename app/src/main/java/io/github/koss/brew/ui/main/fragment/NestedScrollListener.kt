package io.github.koss.brew.ui.main.fragment

interface NestedScrollListener {
    fun onScroll(direction: Direction)
}

enum class Direction {
    UP,
    DOWN
}