package com.example.anidex.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.anidex.AnimeListQuery
import com.example.anidex.ApolloClientProvider
import com.example.anidex.SearchAnimeQuery
import com.example.anidex.navigation.AniDexRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BackgroundColor = Color(0xFF050505)
private val CardColor = Color(0xFF141414)
private val PrimaryRed = Color(0xFFFF3347)
private val AccentRedDark = Color(0xFF7A0000)
private val SoftGray = Color(0xFFD6D9D2)
private val TextWhite = Color(0xFFF7F7F7)
private val TextGray = Color(0xFF9A9A9A)
private val SearchBg = Color(0xFF1C1C1C)

data class AnimeCatalogItem(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val episodes: Int?,
    val score: Int?,
    val season: String?,
    val genres: List<String>
)

data class AnimeScreenBottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun AnimeScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Trending") }
    val selectedIndex = 0

    val animeItems = remember { mutableStateListOf<AnimeCatalogItem>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasNextPage by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    val scope = rememberCoroutineScope()

    val categories = listOf(
        "Trending",
        "Action",
        "Fantasy",
        "Romance",
        "Comedy",
        "Adventure",
        "Drama"
    )

    val navItems = listOf(
        AnimeScreenBottomNavItem("Anime", Icons.Default.PlayArrow, AniDexRoutes.ANIME),
        AnimeScreenBottomNavItem("Manga", Icons.Default.Book, AniDexRoutes.MANGA),
        AnimeScreenBottomNavItem("Accueil", Icons.Default.Home, AniDexRoutes.HOME),
        AnimeScreenBottomNavItem("Favoris", Icons.Default.Favorite, AniDexRoutes.FAVORITES),
        AnimeScreenBottomNavItem("Profil", Icons.Default.Person, AniDexRoutes.PROFILE)
    )

    suspend fun loadAnimePage(
        page: Int,
        targetList: SnapshotStateList<AnimeCatalogItem>,
        replace: Boolean
    ): Boolean {
        val response = ApolloClientProvider.client
            .query(
                AnimeListQuery(
                    page = page,
                    perPage = 20
                )
            )
            .execute()

        val mappedItems = response.data
            ?.Page
            ?.media
            ?.filterNotNull()
            ?.mapNotNull { media ->
                val title = media.title?.english
                    ?: media.title?.romaji
                    ?: media.title?.native
                    ?: return@mapNotNull null

                val genres = media.genres
                    ?.filterNotNull()
                    ?: emptyList()

                AnimeCatalogItem(
                    id = media.id,
                    title = title,
                    imageUrl = media.coverImage?.large,
                    episodes = media.episodes,
                    score = media.averageScore,
                    season = buildSeasonText(media.season?.name, media.seasonYear),
                    genres = genres
                )
            }
            ?: emptyList()

        if (replace) {
            targetList.clear()
        }

        targetList.addAll(mappedItems)
        return response.data?.Page?.pageInfo?.hasNextPage == true
    }

    suspend fun searchAnimeByName(
        query: String,
        targetList: SnapshotStateList<AnimeCatalogItem>
    ) {
        val response = ApolloClientProvider.client
            .query(
                SearchAnimeQuery(
                    page = 1,
                    perPage = 20,
                    search = query
                )
            )
            .execute()

        val mappedItems = response.data
            ?.Page
            ?.media
            ?.filterNotNull()
            ?.mapNotNull { media ->
                val title = media.title?.english
                    ?: media.title?.romaji
                    ?: media.title?.native
                    ?: return@mapNotNull null

                val genres = media.genres
                    ?.filterNotNull()
                    ?: emptyList()

                AnimeCatalogItem(
                    id = media.id,
                    title = title,
                    imageUrl = media.coverImage?.large,
                    episodes = media.episodes,
                    score = media.averageScore,
                    season = buildSeasonText(media.season?.name, media.seasonYear),
                    genres = genres
                )
            }
            ?: emptyList()

        targetList.clear()
        targetList.addAll(mappedItems)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            hasNextPage = loadAnimePage(
                page = 1,
                targetList = animeItems,
                replace = true
            )
            currentPage = 1
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    val displayedItems = animeItems.filter { anime ->
        when (selectedCategory) {
            "Trending" -> true
            else -> anime.genres.any { genre ->
                genre.equals(selectedCategory, ignoreCase = true)
            }
        }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "CATALOGUE ANIME",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Explore trending anime and discover new series.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { value ->
                        searchText = value

                        searchJob?.cancel()
                        searchJob = scope.launch {
                            delay(400)

                            try {
                                errorMessage = null

                                if (searchText.isBlank()) {
                                    isSearching = false
                                    isLoading = true
                                    hasNextPage = loadAnimePage(
                                        page = 1,
                                        targetList = animeItems,
                                        replace = true
                                    )
                                    currentPage = 1
                                    isLoading = false
                                } else {
                                    isSearching = true
                                    isLoading = true
                                    searchAnimeByName(
                                        query = searchText.trim(),
                                        targetList = animeItems
                                    )
                                    hasNextPage = false
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Unknown error"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search an anime",
                            color = TextGray
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search anime",
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

                Spacer(modifier = Modifier.height(18.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            label = category,
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryRed)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isSearching) "Searching anime..." else "Loading anime...",
                                    color = TextWhite
                                )
                            }
                        }
                    }

                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: $errorMessage",
                                color = PrimaryRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(
                                items = displayedItems,
                                key = { it.id }
                            ) { anime ->
                                AnimeCatalogCard(
                                    anime = anime,
                                    onClick = {
                                        navController.navigate("${AniDexRoutes.ANIME_DETAIL}/${anime.id}")
                                    }
                                )
                            }

                            item {
                                if (hasNextPage && !isLoadingMore && !isSearching && selectedCategory == "Trending") {
                                    LoadMoreCard(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    isLoadingMore = true
                                                    errorMessage = null
                                                    val nextPage = currentPage + 1
                                                    hasNextPage = loadAnimePage(
                                                        page = nextPage,
                                                        targetList = animeItems,
                                                        replace = false
                                                    )
                                                    currentPage = nextPage
                                                } catch (e: Exception) {
                                                    errorMessage = e.message ?: "Unknown error"
                                                } finally {
                                                    isLoadingMore = false
                                                }
                                            }
                                        }
                                    )
                                } else if (isLoadingMore) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = PrimaryRed)
                                    }
                                }
                            }
                        }
                    }
                }
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

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) PrimaryRed else CardColor
    val textColor = if (selected) TextWhite else SoftGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AnimeCatalogCard(
    anime: AnimeCatalogItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = anime.imageUrl,
                    contentDescription = anime.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryRed.copy(alpha = 0.92f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = anime.score?.let { "$it%" } ?: "--",
                        color = TextWhite,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = anime.title,
                    color = TextWhite,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    InfoPill(text = anime.episodes?.let { "$it ep" } ?: "N/A")
                    Spacer(modifier = Modifier.width(8.dp))
                    InfoPill(text = anime.season ?: "Unknown")
                }
            }
        }
    }
}

@Composable
fun InfoPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SearchBg)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = SoftGray,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LoadMoreCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Load more",
                color = TextWhite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}