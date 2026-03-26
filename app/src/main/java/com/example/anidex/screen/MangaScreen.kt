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
import com.example.anidex.ApolloClientProvider
import com.example.anidex.MangaListQuery
import com.example.anidex.navigation.AniDexRoutes
import kotlinx.coroutines.launch

private val BackgroundColor = Color(0xFF050505)
private val CardColor = Color(0xFF141414)
private val PrimaryRed = Color(0xFFFF3347)
private val AccentRedDark = Color(0xFF7A0000)
private val SoftGray = Color(0xFFD6D9D2)
private val TextWhite = Color(0xFFF7F7F7)
private val TextGray = Color(0xFF9A9A9A)
private val SearchBg = Color(0xFF1C1C1C)

data class MangaCatalogItem(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val chapters: Int?,
    val volumes: Int?,
    val score: Int?,
    val status: String?,
    val genres: List<String>
)

data class MangaBottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun MangaScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Trending") }
    val selectedIndex = 1

    val mangaItems = remember { mutableStateListOf<MangaCatalogItem>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasNextPage by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }

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
        MangaBottomNavItem("Anime", Icons.Default.PlayArrow, AniDexRoutes.ANIME),
        MangaBottomNavItem("Manga", Icons.Default.Book, AniDexRoutes.MANGA),
        MangaBottomNavItem("Accueil", Icons.Default.Home, AniDexRoutes.HOME),
        MangaBottomNavItem("Favoris", Icons.Default.Favorite, AniDexRoutes.FAVORITES),
        MangaBottomNavItem("Profil", Icons.Default.Person, AniDexRoutes.PROFILE)
    )

    suspend fun loadMangaPage(
        page: Int,
        targetList: SnapshotStateList<MangaCatalogItem>,
        replace: Boolean
    ): Boolean {
        val response = ApolloClientProvider.client
            .query(
                MangaListQuery(
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

                MangaCatalogItem(
                    id = media.id,
                    title = title,
                    imageUrl = media.coverImage?.large,
                    chapters = media.chapters,
                    volumes = media.volumes,
                    score = media.averageScore,
                    status = buildStatusText(media.status?.name),
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

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            hasNextPage = loadMangaPage(
                page = 1,
                targetList = mangaItems,
                replace = true
            )
            currentPage = 1
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    val filteredItems = mangaItems.filter { manga ->
        val matchesSearch = manga.title.contains(searchText, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "Trending" -> true
            else -> manga.genres.any { genre ->
                genre.equals(selectedCategory, ignoreCase = true)
            }
        }

        matchesSearch && matchesCategory
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
                    text = "CATALOGUE MANGA",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Explore trending manga and discover new stories.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search a manga",
                            color = TextGray
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search manga",
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
                        MangaCategoryChip(
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
                                    text = "Loading manga...",
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
                                items = filteredItems,
                                key = { it.id }
                            ) { manga ->
                                MangaCatalogCard(
                                    manga = manga,
                                    onClick = {
                                        // navController.navigate("manga_details/${manga.id}")
                                    }
                                )
                            }

                            item {
                                if (hasNextPage && !isLoadingMore && searchText.isBlank() && selectedCategory == "Trending") {
                                    MangaLoadMoreCard(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    isLoadingMore = true
                                                    errorMessage = null
                                                    val nextPage = currentPage + 1
                                                    hasNextPage = loadMangaPage(
                                                        page = nextPage,
                                                        targetList = mangaItems,
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

private fun buildStatusText(status: String?): String {
    return when (status) {
        "FINISHED" -> "Finished"
        "RELEASING" -> "Releasing"
        "NOT_YET_RELEASED" -> "Upcoming"
        "CANCELLED" -> "Cancelled"
        "HIATUS" -> "Hiatus"
        else -> "Unknown"
    }
}

@Composable
fun MangaCategoryChip(
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
fun MangaCatalogCard(
    manga: MangaCatalogItem,
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
                    model = manga.imageUrl,
                    contentDescription = manga.title,
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
                        text = manga.score?.let { "$it%" } ?: "--",
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
                    text = manga.title,
                    color = TextWhite,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    MangaInfoPill(text = manga.chapters?.let { "$it ch" } ?: "N/A")
                    Spacer(modifier = Modifier.width(8.dp))
                    MangaInfoPill(text = manga.volumes?.let { "$it vol" } ?: "N/A")
                }

                Spacer(modifier = Modifier.height(8.dp))

                MangaInfoPill(text = manga.status ?: "Unknown")
            }
        }
    }
}

@Composable
fun MangaInfoPill(text: String) {
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
fun MangaLoadMoreCard(
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