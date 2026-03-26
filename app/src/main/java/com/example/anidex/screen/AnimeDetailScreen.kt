package com.example.anidex.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.anidex.AnimeDetailsQuery
import com.example.anidex.ApolloClientProvider
import com.example.anidex.navigation.AniDexRoutes

private val DetailBackgroundColor = Color(0xFF050505)
private val DetailCardColor = Color(0xFF141414)
private val DetailPrimaryRed = Color(0xFFFF3347)
private val DetailAccentRedDark = Color(0xFF7A0000)
private val DetailSoftGray = Color(0xFFD6D9D2)
private val DetailTextWhite = Color(0xFFF7F7F7)
private val DetailTextGray = Color(0xFF9A9A9A)
private val DetailSearchBg = Color(0xFF1C1C1C)

data class AnimeDetailUiModel(
    val id: Int,
    val title: String,
    val nativeTitle: String?,
    val description: String?,
    val coverImage: String?,
    val bannerImage: String?,
    val episodes: Int?,
    val duration: Int?,
    val status: String?,
    val format: String?,
    val source: String?,
    val season: String?,
    val seasonYear: Int?,
    val averageScore: Int?,
    val meanScore: Int?,
    val popularity: Int?,
    val favourites: Int?,
    val genres: List<String>,
    val studio: String?,
    val startDate: String?,
    val endDate: String?,
    val trailerSite: String?,
    val relationItems: List<RelatedAnimeItem>
)

data class RelatedAnimeItem(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val relationType: String?,
    val format: String?
)

data class AnimeDetailBottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun AnimeDetailScreen(
    navController: NavHostController,
    animeId: Int
) {
    var anime by remember { mutableStateOf<AnimeDetailUiModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val navItems = listOf(
        AnimeDetailBottomNavItem("Anime", Icons.Default.PlayArrow, AniDexRoutes.ANIME),
        AnimeDetailBottomNavItem("Manga", Icons.Default.Book, AniDexRoutes.MANGA),
        AnimeDetailBottomNavItem("Accueil", Icons.Default.Home, AniDexRoutes.HOME),
        AnimeDetailBottomNavItem("Favoris", Icons.Default.Favorite, AniDexRoutes.FAVORITES),
        AnimeDetailBottomNavItem("Profil", Icons.Default.Person, AniDexRoutes.PROFILE)
    )

    LaunchedEffect(animeId) {
        try {
            isLoading = true
            errorMessage = null

            val response = ApolloClientProvider.client
                .query(AnimeDetailsQuery(id = animeId))
                .execute()

            val media = response.data?.Media

            if (media == null) {
                errorMessage = "Anime introuvable"
            } else {
                anime = AnimeDetailUiModel(
                    id = media.id,
                    title = media.title?.english
                        ?: media.title?.romaji
                        ?: media.title?.native
                        ?: "Unknown title",
                    nativeTitle = media.title?.native,
                    description = media.description,
                    coverImage = media.coverImage?.extraLarge
                        ?: media.coverImage?.large
                        ?: media.coverImage?.medium,
                    bannerImage = media.bannerImage,
                    episodes = media.episodes,
                    duration = media.duration,
                    status = media.status?.name?.replace("_", " "),
                    format = media.format?.name?.replace("_", " "),
                    source = media.source?.name?.replace("_", " "),
                    season = media.season?.name?.replace("_", " "),
                    seasonYear = media.seasonYear,
                    averageScore = media.averageScore,
                    meanScore = media.meanScore,
                    popularity = media.popularity,
                    favourites = media.favourites,
                    genres = media.genres?.filterNotNull() ?: emptyList(),
                    studio = media.studios?.nodes
                        ?.filterNotNull()
                        ?.firstOrNull()
                        ?.name,
                    startDate = formatDate(
                        media.startDate?.day,
                        media.startDate?.month,
                        media.startDate?.year
                    ),
                    endDate = formatDate(
                        media.endDate?.day,
                        media.endDate?.month,
                        media.endDate?.year
                    ),
                    trailerSite = media.trailer?.site,
                    relationItems = media.relations?.edges
                        ?.filterNotNull()
                        ?.mapNotNull { edge ->
                            val node = edge.node ?: return@mapNotNull null
                            val relatedTitle = node.title?.english
                                ?: node.title?.romaji
                                ?: node.title?.native
                                ?: return@mapNotNull null

                            RelatedAnimeItem(
                                id = node.id,
                                title = relatedTitle,
                                imageUrl = node.coverImage?.large ?: node.coverImage?.medium,
                                relationType = edge.relationType?.name?.replace("_", " "),
                                format = node.format?.name?.replace("_", " ")
                            )
                        }
                        ?: emptyList()
                )
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erreur inconnue"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = DetailBackgroundColor,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            NavigationBar(
                containerColor = DetailAccentRedDark,
                tonalElevation = 0.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == 0,
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
                            selectedIconColor = DetailTextWhite,
                            selectedTextColor = DetailTextWhite,
                            unselectedIconColor = DetailSoftGray,
                            unselectedTextColor = DetailSoftGray,
                            indicatorColor = DetailPrimaryRed
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DetailBackgroundColor
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = DetailPrimaryRed)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading details...",
                                color = DetailTextWhite
                            )
                        }
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $errorMessage",
                            color = DetailPrimaryRed,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                anime != null -> {
                    val animeData = anime!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        ) {
                            AsyncImage(
                                model = animeData.bannerImage ?: animeData.coverImage,
                                contentDescription = animeData.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.15f),
                                                Color.Black.copy(alpha = 0.45f),
                                                DetailBackgroundColor
                                            )
                                        )
                                    )
                            )

                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp)
                                    .windowInsetsPadding(WindowInsets.statusBars)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .align(Alignment.TopStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Retour",
                                    tint = DetailTextWhite
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 20.dp, vertical = 18.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    AsyncImage(
                                        model = animeData.coverImage,
                                        contentDescription = animeData.title,
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(170.dp)
                                            .clip(RoundedCornerShape(18.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = animeData.title,
                                            color = DetailTextWhite,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (!animeData.nativeTitle.isNullOrBlank() &&
                                            animeData.nativeTitle != animeData.title
                                        ) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = animeData.nativeTitle,
                                                color = DetailSoftGray,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            DetailBadge(text = animeData.averageScore?.let { "$it%" } ?: "--")
                                            DetailBadge(text = animeData.format ?: "Unknown")
                                        }
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Spacer(modifier = Modifier.height(14.dp))

                            if (animeData.genres.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(end = 8.dp)
                                ) {
                                    items(animeData.genres) { genre ->
                                        GenreChip(label = genre)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DetailInfoCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Episodes",
                                    value = animeData.episodes?.toString() ?: "N/A"
                                )
                                DetailInfoCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Duration",
                                    value = animeData.duration?.let { "$it min" } ?: "N/A"
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DetailInfoCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Season",
                                    value = buildSeasonText(animeData.season, animeData.seasonYear)
                                )
                                DetailInfoCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Status",
                                    value = animeData.status ?: "Unknown"
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DetailInfoCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Studio",
                                    value = animeData.studio ?: "Unknown"
                                )
                                DetailInfoCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Source",
                                    value = animeData.source ?: "Unknown"
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Synopsis",
                                color = DetailTextWhite,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = DetailCardColor),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = animeData.description
                                        ?.replace("<br>", "\n")
                                        ?.replace("<br><br>", "\n\n")
                                        ?.replace("<i>", "")
                                        ?.replace("</i>", "")
                                        ?.replace("<b>", "")
                                        ?.replace("</b>", "")
                                        ?.replace("&quot;", "\"")
                                        ?.replace("&#039;", "'")
                                        ?: "No description available.",
                                    color = DetailSoftGray,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "More info",
                                color = DetailTextWhite,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = DetailCardColor),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    InfoRow("Mean score", animeData.meanScore?.let { "$it%" } ?: "N/A")
                                    InfoRow("Popularity", animeData.popularity?.toString() ?: "N/A")
                                    InfoRow("Favourites", animeData.favourites?.toString() ?: "N/A")
                                    InfoRow("Start date", animeData.startDate ?: "N/A")
                                    InfoRow("End date", animeData.endDate ?: "N/A")
                                    InfoRow("Trailer", animeData.trailerSite ?: "N/A")
                                }
                            }

                            if (animeData.relationItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Related anime",
                                    color = DetailTextWhite,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(end = 8.dp)
                                ) {
                                    items(animeData.relationItems) { item ->
                                        RelatedAnimeCard(
                                            item = item,
                                            onClick = {
                                                navController.navigate("${AniDexRoutes.ANIME_DETAIL}/${item.id}")
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DetailPrimaryRed.copy(alpha = 0.92f))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = DetailTextWhite,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GenreChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DetailSearchBg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = DetailSoftGray,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DetailInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DetailCardColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                color = DetailTextGray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = DetailTextWhite,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = DetailTextGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            color = DetailTextWhite,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RelatedAnimeCard(
    item: RelatedAnimeItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DetailCardColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = item.title,
                    color = DetailTextWhite,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = listOfNotNull(item.relationType, item.format).joinToString(" • "),
                    color = DetailSoftGray,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun buildSeasonText(season: String?, seasonYear: Int?): String {
    return when {
        !season.isNullOrBlank() && seasonYear != null -> "$season $seasonYear"
        !season.isNullOrBlank() -> season
        seasonYear != null -> seasonYear.toString()
        else -> "Unknown"
    }
}

private fun formatDate(day: Int?, month: Int?, year: Int?): String? {
    if (year == null) return null
    val dayText = day?.toString()?.padStart(2, '0') ?: "??"
    val monthText = month?.toString()?.padStart(2, '0') ?: "??"
    return "$dayText/$monthText/$year"
}