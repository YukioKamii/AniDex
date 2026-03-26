package com.example.anidex.navigation

import com.example.anidex.screen.FavoritesScreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.anidex.screen.AnimeScreen
import com.example.anidex.screen.HomeScreen
import com.example.anidex.screen.MangaScreen
import com.example.anidex.screen.ProfileScreen

object AniDexRoutes {
    const val HOME = "home"
    const val ANIME = "anime"
    const val MANGA = "manga"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
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