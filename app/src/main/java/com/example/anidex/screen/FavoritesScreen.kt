package com.example.anidex.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.anidex.data.local.database.DatabaseProvider
import com.example.anidex.data.local.entity.FavoriteEntity
import com.example.anidex.data.local.session.UserSessionManager
import com.example.anidex.navigation.AniDexRoutes

private val BackgroundColor = Color(0xFF050505)
private val CardColor = Color(0xFF141414)
private val PrimaryRed = Color(0xFFFF3347)
private val AccentRedDark = Color(0xFF7A0000)
private val SoftGray = Color(0xFFD6D9D2)
private val TextWhite = Color(0xFFF7F7F7)
private val TextGray = Color(0xFF9A9A9A)
private val SearchBg = Color(0xFF1C1C1C)

@Composable
fun FavoritesScreen(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val favoriteDao = remember { database.favoriteDao() }
    val sessionManager = remember { UserSessionManager(context) }

    var currentUserId by rememberSaveable { mutableStateOf(sessionManager.getCurrentUserId()) }
    var showAuthDialog by rememberSaveable { mutableStateOf(false) }

    val favorites by if (currentUserId != null) {
        favoriteDao.getFavoritesByUser(currentUserId!!).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    LaunchedEffect(Unit) {
        currentUserId = sessionManager.getCurrentUserId()
        showAuthDialog = currentUserId == null
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundColor
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (currentUserId == null) {
                    LockedFavoritesContent(innerPadding = innerPadding)
                } else {
                    FavoritesConnectedContent(
                        innerPadding = innerPadding,
                        favorites = favorites
                    )
                }

                if (showAuthDialog && currentUserId == null) {
                    AuthRequiredDialog(
                        title = "Connexion requise",
                        message = "Connecte-toi pour accéder à tes favoris AniDex et retrouver tous tes anime et mangas enregistrés.",
                        onDismiss = {
                            showAuthDialog = false
                            navController.popBackStack()
                        },
                        onLoginClick = {
                            showAuthDialog = false
                            navController.navigate(AniDexRoutes.AUTH)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LockedFavoritesContent(
    innerPadding: androidx.compose.foundation.layout.PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .graphicsLayer { alpha = 0.35f }
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Favoris",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextWhite
        )

        Text(
            text = "Retrouve ici tous tes coups de cœur anime et manga.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        repeat(4) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 74.dp, height = 104.dp)
                            .background(SearchBg, RoundedCornerShape(14.dp))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Favori verrouillé",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connecte-toi pour afficher ce contenu.",
                            color = TextGray,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesConnectedContent(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    favorites: List<FavoriteEntity>
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Aucun favori pour l’instant",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ajoute des anime ou des mangas à tes favoris pour les retrouver ici.",
                    color = TextGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Favoris",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "${favorites.size} élément(s) enregistré(s)",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                )
            }

            items(favorites, key = { it.id }) { favorite ->
                FavoriteCard(favorite = favorite)
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun FavoriteCard(favorite: FavoriteEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = favorite.imageUrl,
                contentDescription = favorite.title,
                modifier = Modifier
                    .size(width = 74.dp, height = 104.dp)
                    .background(SearchBg, RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = favorite.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = favorite.type,
                    color = PrimaryRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )

                favorite.subtitle?.let {
                    Text(
                        text = it,
                        color = TextGray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    favorite.score?.let {
                        FavoriteInfoPill(text = "$it%")
                    }

                    when (favorite.type) {
                        "ANIME" -> {
                            favorite.episodes?.let {
                                FavoriteInfoPill(text = "$it ep")
                            }
                        }

                        "MANGA" -> {
                            favorite.chapters?.let {
                                FavoriteInfoPill(text = "$it ch")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteInfoPill(text: String) {
    Box(
        modifier = Modifier
            .background(SearchBg, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = SoftGray,
            style = MaterialTheme.typography.labelMedium
        )
    }
}