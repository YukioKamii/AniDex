package com.example.anidex.screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.anidex.R
import com.example.anidex.navigation.AniDexRoutes
import kotlinx.coroutines.delay

private val BackgroundColor = Color(0xFF050505)
private val CardColor = Color(0xFF141414)
private val PrimaryRed = Color(0xFFFF3347)
private val AccentRedDark = Color(0xFF7A0000)
private val SoftGray = Color(0xFFD6D9D2)
private val TextWhite = Color(0xFFF7F7F7)
private val TextGray = Color(0xFF9A9A9A)
private val SearchBg = Color(0xFF1C1C1C)

data class TrendingItem(
    val title: String,
    val subtitle: String
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun HomeScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val selectedIndex = 2

    val trendingItems = listOf(
        TrendingItem("Solo Leveling", "Action • Fantasy"),
        TrendingItem("Frieren", "Adventure • Fantasy"),
        TrendingItem("Blue Lock", "Sport • Shonen"),
        TrendingItem("Jujutsu Kaisen", "Action • Supernatural"),
        TrendingItem("One Piece", "Adventure • Shonen")
    )

    val navItems = listOf(
        BottomNavItem("Anime", Icons.Default.PlayArrow, AniDexRoutes.ANIME),
        BottomNavItem("Manga", Icons.Default.Book, AniDexRoutes.MANGA),
        BottomNavItem("Accueil", Icons.Default.Home, AniDexRoutes.HOME),
        BottomNavItem("Favoris", Icons.Default.Favorite, AniDexRoutes.FAVORITES),
        BottomNavItem("Profil", Icons.Default.Person, AniDexRoutes.PROFILE)
    )

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    HeaderSection()
                }

                item {
                    SearchSection(
                        searchText = searchText,
                        onSearchChange = { searchText = it }
                    )
                }

                item {
                    CategorySection(navController = navController)
                }

                item {
                    Text(
                        text = "TENDANCES DU MOMENT",
                        color = TextWhite,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(trendingItems) { item ->
                    TrendingCard(item = item)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo AniDex",
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp), // ~30% plus grand
        contentScale = ContentScale.Fit
    )
}

@Composable
fun SearchSection(
    searchText: String,
    onSearchChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Rechercher un anime ou un manga",
                color = TextGray
            )
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Recherche",
                tint = SoftGray
            )
        },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBg,
            unfocusedContainerColor = SearchBg,
            disabledContainerColor = SearchBg,
            focusedBorderColor = PrimaryRed,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = PrimaryRed,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}

@Composable
fun CategorySection(navController: NavHostController) {
    // Remplace ensuite ces images par tes vraies covers/anime posters
    val animeImages = listOf(
        R.drawable.anime1,
        R.drawable.anime2,
        R.drawable.anime3,
        R.drawable.anime4,
        R.drawable.anime5
    )

    val mangaImages = listOf(
        R.drawable.manga1,
        R.drawable.manga2,
        R.drawable.manga3,
        R.drawable.manga4,
        R.drawable.manga5
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MediaCarouselCard(
            title = "Anime",
            subtitle = "Séries et films",
            icon = Icons.Default.PlayArrow,
            images = animeImages,
            modifier = Modifier.weight(1f),
            highlight = true,
            onClick = { navController.navigate(AniDexRoutes.ANIME) }
        )

        MediaCarouselCard(
            title = "Manga",
            subtitle = "Volumes et lectures",
            icon = Icons.Default.Book,
            images = mangaImages,
            modifier = Modifier.weight(1f),
            highlight = false,
            onClick = { navController.navigate(AniDexRoutes.MANGA) }
        )
    }
}

@Composable
fun MediaCarouselCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    images: List<Int>,
    modifier: Modifier = Modifier,
    highlight: Boolean,
    onClick: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(images) {
        if (images.isNotEmpty()) {
            while (true) {
                delay(2500)
                currentIndex = (currentIndex + 1) % images.size
            }
        }
    }

    val subTextColor = if (highlight) TextWhite.copy(alpha = 0.88f) else SoftGray
    val iconBg = if (highlight) AccentRedDark.copy(alpha = 0.45f) else PrimaryRed.copy(alpha = 0.22f)

    Card(
        modifier = modifier
            .height(210.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (images.isNotEmpty()) {
                Image(
                    painter = painterResource(id = images[currentIndex]),
                    contentDescription = "$title background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // overlay sombre pour garder la lisibilité
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (highlight) Color.Black.copy(alpha = 0.42f)
                        else Color.Black.copy(alpha = 0.55f)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = iconBg,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = TextWhite
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = TextWhite,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        color = subTextColor,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CarouselDots(
                        total = images.size,
                        selectedIndex = currentIndex
                    )
                }
            }
        }
    }
}

@Composable
fun CarouselDots(
    total: Int,
    selectedIndex: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == selectedIndex) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == selectedIndex) TextWhite
                        else TextWhite.copy(alpha = 0.40f)
                    )
            )
        }
    }
}

@Composable
fun TrendingCard(item: TrendingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = PrimaryRed,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "IMG",
                    color = TextWhite,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}