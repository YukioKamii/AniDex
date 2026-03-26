package com.example.anidex.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF6F7FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Profil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F1F39)
            )

            Text(
                text = "Ton espace personnel AniDex",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B6B8A),
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            ProfileHeaderCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Statistiques",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F39)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatCard(
                    title = "Anime vus",
                    value = "128",
                    icon = Icons.Default.Movie,
                    gradientColors = listOf(Color(0xFF7F5AF0), Color(0xFF6246EA)),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Favoris",
                    value = "24",
                    icon = Icons.Default.Favorite,
                    gradientColors = listOf(Color(0xFFFF6B81), Color(0xFFFF4D6D)),
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
                    gradientColors = listOf(Color(0xFFFF8906), Color(0xFFF25F4C)),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "En cours",
                    value = "9",
                    icon = Icons.Default.Person,
                    gradientColors = listOf(Color(0xFF2CB67D), Color(0xFF00A896)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Activité récente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F39)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActivityCard(
                title = "Solo Leveling",
                subtitle = "Épisode 9 regardé",
                accentColor = Color(0xFF6246EA)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActivityCard(
                title = "Blue Lock",
                subtitle = "Ajouté aux favoris",
                accentColor = Color(0xFFFF6B81)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActivityCard(
                title = "Frieren",
                subtitle = "Mise à jour de la progression",
                accentColor = Color(0xFFFF8906)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Paramètres",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F39)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                            listOf(Color(0xFF7F5AF0), Color(0xFF6246EA))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "T",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tung Sahur",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F1F39)
            )

            Text(
                text = "@tung_anidex",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B6B8A),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Passionné d’anime et de manga, toujours à la recherche de nouvelles pépites ✨",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B6B8A),
                modifier = Modifier.padding(top = 12.dp),
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFF6F7FB),
                shape = RoundedCornerShape(50)
            )
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF6246EA),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            color = Color(0xFF1F1F39),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
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
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
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
                        color = accentColor.copy(alpha = 0.14f),
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
                    color = Color(0xFF1F1F39)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B6B8A)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F39))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Préférences du compte",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Gère ton profil, tes notifications et tes préférences de lecture.",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
