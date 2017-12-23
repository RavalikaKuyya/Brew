package io.github.jamiesanson.brew.ui.main.fragment

interface NestedScrollListener {
    fun onScroll(direction: Direction)
}

enum class Direction {
    UP,
    DOWN
}