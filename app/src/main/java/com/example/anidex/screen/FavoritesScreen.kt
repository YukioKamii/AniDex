package com.example.anidex.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import com.example.anidex.data.local.session.UserSessionManager
import com.example.anidex.navigation.AniDexRoutes

private val BackgroundColor = Color(0xFF050505)
private val PrimaryRed = Color(0xFFFF3347)
private val AccentRedDark = Color(0xFF7A0000)
private val SoftGray = Color(0xFFD6D9D2)
private val TextWhite = Color(0xFFF7F7F7)

@Composable
fun FavoritesScreen(navController: NavHostController) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    var showDialog by remember { mutableStateOf(false) }
    val isLoggedIn = sessionManager.getCurrentUserId() != null

    // 🔥 Popup si pas connecté
    LaunchedEffect(Unit) {
        if (!isLoggedIn) {
            showDialog = true
        }
    }

    if (showDialog && !isLoggedIn) {
        AuthRequiredDialog(
            title = "Connexion requise",
            message = "Connecte-toi pour accéder à tes favoris ❤",
            onDismiss = {
                navController.popBackStack()
            },
            onLoginClick = {
                navController.navigate(AniDexRoutes.AUTH)
            }
        )
    }

    Scaffold(
        containerColor = BackgroundColor,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            NavigationBar(
                containerColor = AccentRedDark,
                tonalElevation = 0.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AniDexRoutes.ANIME) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Anime") },
                    label = { Text("Anime") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AniDexRoutes.MANGA) },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Manga") },
                    label = { Text("Manga") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AniDexRoutes.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoris") },
                    label = { Text("Favoris") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        unselectedIconColor = SoftGray,
                        unselectedTextColor = SoftGray,
                        indicatorColor = PrimaryRed
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AniDexRoutes.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { innerPadding ->

        if (isLoggedIn) {
            // 👉 PLUS TARD : vraie liste favoris
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MES FAVORIS ❤",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        } else {
            // écran vide derrière popup
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {}
        }
    }
}