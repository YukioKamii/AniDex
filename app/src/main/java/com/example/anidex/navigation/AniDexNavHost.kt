package com.example.anidex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.anidex.screen.AnimeDetailScreen
import com.example.anidex.screen.AnimeScreen
import com.example.anidex.screen.FavoritesScreen
import com.example.anidex.screen.HomeScreen
import com.example.anidex.screen.MangaScreen
import com.example.anidex.screen.ProfileScreen

object AniDexRoutes {
    const val HOME = "home"
    const val ANIME = "anime"
    const val MANGA = "manga"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val ANIME_DETAIL = "anime_detail"
}

@Composable
fun AniDexNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AniDexRoutes.HOME
    ) {
        composable(AniDexRoutes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(AniDexRoutes.ANIME) {
            AnimeScreen(navController = navController)
        }

        composable(
            route = "${AniDexRoutes.ANIME_DETAIL}/{animeId}",
            arguments = listOf(
                navArgument("animeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getInt("animeId") ?: 0

            AnimeDetailScreen(
                navController = navController,
                animeId = animeId
            )
        }

        composable(AniDexRoutes.MANGA) {
            MangaScreen(navController = navController)
        }

        composable(AniDexRoutes.FAVORITES) {
            FavoritesScreen(navController = navController)
        }

        composable(AniDexRoutes.PROFILE) {
            ProfileScreen(navController = navController)
        }
    }
}