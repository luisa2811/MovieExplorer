package com.movieexplorer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movieexplorer.ui.detail.MovieDetailScreen
import com.movieexplorer.ui.list.MovieListScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(Routes.LIST) {
            MovieListScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Routes.detail(movieId))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) {
            MovieDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
