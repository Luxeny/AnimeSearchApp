package com.example.animesearchapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.animesearchapp.presentation.ui.details.DetailsScreen
import com.example.animesearchapp.presentation.ui.search.SearchScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.SEARCH
    ) {
        composable(Destinations.SEARCH) {
            SearchScreen(
                onAnimeClick = { id -> navController.navigate(Destinations.detailsRoute(id)) }
            )
        }

        composable(
            route = Destinations.detailsPattern,
            arguments = listOf(navArgument(Destinations.ARG_ANIME_ID) { type = NavType.IntType })
        ) {
            DetailsScreen(onBack = { navController.popBackStack() })
        }
    }
}
