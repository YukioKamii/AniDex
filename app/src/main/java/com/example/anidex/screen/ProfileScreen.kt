package com.example.anidex.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.anidex.navigation.AniDexRoutes

private val BackgroundColor = Color(0xFF050505)
private val CardColor = Color(0xFF141414)
private val PrimaryRed = Color(0xFFFF3347)
private val AccentRedDark = Color(0xFF7A0000)
private val SoftGray = Color(0xFFD6D9D2)
private val TextWhite = Color(0xFFF7F7F7)
private val TextGray = Color(0xFF9A9A9A)
private val MutedBorder = Color(0xFF232323)

data class ProfileBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun ProfileScreen(navController: NavHostController) {
    val navItems = listOf(
        ProfileBottomNavItem("Anime", Icons.Default.PlayArrow, AniDexRoutes.ANIME),
        ProfileBottomNavItem("Manga", Icons.Default.Book, AniDexRoutes.MANGA),
        ProfileBottomNavItem("Accueil", Icons.Default.Home, AniDexRoutes.HOME),
        ProfileBottomNavItem("Favoris", Icons.Default.Favorite, AniDexRoutes.FAVORITES),
        ProfileBottomNavItem("Profil", Icons.Default.Person, AniDexRoutes.PROFILE)
    )

    var selectedIndex by remember { mutableIntStateOf(4) }

    Scaffold(
        containerColor = BackgroundColor,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            NavigationBar(
                containerColor = AccentRedDark,
                tonalElevation = 0.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(text = item.label, maxLines = 1)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextWhite,
                            selectedTextColor = TextWhite,
                            unselectedIconColor = SoftGray,
                            unselectedTextColor = SoftGray,
                            indicatorColor = PrimaryRed
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Profil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )

                Text(
                    text = "Ton espace personnel AniDex",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                ProfileHeaderCard()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Statistiques",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Anime vus",
                        value = "128",
                        icon = Icons.Default.PlayArrow,
                        gradientColors = listOf(Color(0xFFFF4B5C), Color(0xFFB3122D)),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Favoris",
                        value = "24",
                        icon = Icons.Default.Favorite,
                        gradientColors = listOf(Color(0xFF1F1F1F), Color(0xFF111111)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Mangas lus",
                        value = "56",
                        icon = Icons.Default.Bookmark,
                        gradientColors = listOf(Color(0xFFFF7A18), Color(0xFFAF002D)),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "En cours",
                        value = "9",
                        icon = Icons.Default.Person,
                        gradientColors = listOf(Color(0xFF2C2C2C), Color(0xFF191919)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Activité récente",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActivityCard(
                    title = "Solo Leveling",
                    subtitle = "Épisode 9 regardé",
                    accentColor = PrimaryRed
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActivityCard(
                    title = "Blue Lock",
                    subtitle = "Ajouté aux favoris",
                    accentColor = Color(0xFFFF7A18)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActivityCard(
                    title = "Frieren",
                    subtitle = "Mise à jour de la progression",
                    accentColor = Color(0xFFD6D9D2)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Paramètres",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsCard()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProfileHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(PrimaryRed, AccentRedDark)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "T",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tung Sahur",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )

            Text(
                text = "@tung_anidex",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Passionné d’anime et de manga, toujours à la recherche de nouvelles pépites ✨",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftGray,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileActionChip(
                    icon = Icons.Default.Edit,
                    label = "Modifier"
                )

                ProfileActionChip(
                    icon = Icons.Default.Settings,
                    label = "Réglages"
                )
            }
        }
    }
}

@Composable
fun ProfileActionChip(
    icon: ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFF1C1C1C),
                shape = RoundedCornerShape(50)
            )
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryRed,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            color = TextWhite,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                )
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = TextWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    title: String,
    subtitle: String,
    accentColor: Color
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = CardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(1),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun SettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Préférences du compte",
                color = TextWhite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Gère ton profil, tes notifications et tes préférences de lecture.",
                color = TextGray,
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MutedBorder)
            )

            Text(
                text = "Thème sombre activé • Notifications personnalisées • Synchronisation AniDex",
                color = SoftGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}