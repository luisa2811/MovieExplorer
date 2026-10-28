package com.movieexplorer.ui.navigation

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{movieId}"

    fun detail(movieId: Int): String = "detail/$movieId"
}
