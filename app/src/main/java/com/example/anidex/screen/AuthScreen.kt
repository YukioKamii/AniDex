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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.anidex.data.local.database.DatabaseProvider
import com.example.anidex.data.local.entity.HistoryEntity
import com.example.anidex.data.local.entity.UserEntity
import com.example.anidex.data.local.session.UserSessionManager
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
private val MutedBorder = Color(0xFF232323)

@Composable
fun AuthScreen(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val userDao = remember { database.userDao() }
    val historyDao = remember { database.historyDao() }
    val sessionManager = remember { UserSessionManager(context) }
    val scope = rememberCoroutineScope()

    var isLoginMode by remember { mutableStateOf(true) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var bio by remember {
        mutableStateOf("Passionné d’anime et de manga, toujours à la recherche de nouvelles pépites ✨")
    }
    var darkThemeEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = BackgroundColor,
        contentWindowInsets = WindowInsets.statusBars
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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .background(
                            color = CardColor,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isLoginMode) "Connexion" else "Créer un compte",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isLoginMode)
                        "Connecte-toi pour accéder à ton profil et à tes favoris."
                    else
                        "Crée ton compte local AniDex pour enregistrer ton profil, tes favoris et ton activité.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(PrimaryRed, AccentRedDark)
                                    ),
                                    shape = CircleShape
                                )
                                .padding(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = TextWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        AuthTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = null
                                successMessage = null
                            },
                            label = "Email"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        AuthTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                                successMessage = null
                            },
                            label = "Mot de passe"
                        )

                        if (!isLoginMode) {
                            Spacer(modifier = Modifier.height(12.dp))

                            AuthTextField(
                                value = displayName,
                                onValueChange = {
                                    displayName = it
                                    errorMessage = null
                                    successMessage = null
                                },
                                label = "Nom affiché"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AuthTextField(
                                value = username,
                                onValueChange = {
                                    username = it
                                    errorMessage = null
                                    successMessage = null
                                },
                                label = "Pseudo"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AuthTextField(
                                value = bio,
                                onValueChange = {
                                    bio = it
                                    errorMessage = null
                                    successMessage = null
                                },
                                label = "Bio",
                                singleLine = false,
                                minLines = 3
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            AuthSwitchRow(
                                title = "Thème sombre",
                                checked = darkThemeEnabled,
                                onCheckedChange = { darkThemeEnabled = it }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            AuthSwitchRow(
                                title = "Notifications",
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = errorMessage!!,
                                color = PrimaryRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (successMessage != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = successMessage!!,
                                color = SoftGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        errorMessage = null
                                        successMessage = null

                                        if (email.isBlank() || password.isBlank()) {
                                            errorMessage = "Merci de remplir l’email et le mot de passe."
                                            return@launch
                                        }

                                        if (isLoginMode) {
                                            val user = userDao.login(
                                                email = email.trim(),
                                                password = password.trim()
                                            )

                                            if (user == null) {
                                                errorMessage = "Email ou mot de passe incorrect."
                                                return@launch
                                            }

                                            sessionManager.saveCurrentUserId(user.id)

                                            historyDao.insertHistory(
                                                HistoryEntity(
                                                    userId = user.id,
                                                    title = "Connexion",
                                                    type = "PROFILE",
                                                    actionType = "LOGIN",
                                                    details = "Connexion au compte ${user.username}"
                                                )
                                            )

                                            navController.navigate(AniDexRoutes.PROFILE) {
                                                popUpTo(AniDexRoutes.AUTH) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        } else {
                                            if (displayName.isBlank() || username.isBlank()) {
                                                errorMessage = "Merci de remplir le nom affiché et le pseudo."
                                                return@launch
                                            }

                                            val existingUser = userDao.getUserByEmail(email.trim())
                                            if (existingUser != null) {
                                                errorMessage = "Un compte existe déjà avec cet email."
                                                return@launch
                                            }

                                            val newUser = UserEntity(
                                                email = email.trim(),
                                                password = password.trim(),
                                                displayName = displayName.trim(),
                                                username = if (username.trim().startsWith("@")) {
                                                    username.trim()
                                                } else {
                                                    "@${username.trim()}"
                                                },
                                                bio = bio.trim(),
                                                darkThemeEnabled = darkThemeEnabled,
                                                notificationsEnabled = notificationsEnabled
                                            )

                                            val userId = userDao.insertUser(newUser).toInt()
                                            sessionManager.saveCurrentUserId(userId)

                                            historyDao.insertHistory(
                                                HistoryEntity(
                                                    userId = userId,
                                                    title = "Inscription",
                                                    type = "PROFILE",
                                                    actionType = "REGISTER",
                                                    details = "Création du compte ${newUser.username}"
                                                )
                                            )

                                            navController.navigate(AniDexRoutes.PROFILE) {
                                                popUpTo(AniDexRoutes.AUTH) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = e.message ?: "Une erreur est survenue."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryRed,
                                contentColor = TextWhite
                            )
                        ) {
                            Text(
                                text = if (isLoginMode) "Se connecter" else "Créer le compte",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                isLoginMode = !isLoginMode
                                errorMessage = null
                                successMessage = null
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (isLoginMode)
                                    "Pas encore de compte ? S’inscrire"
                                else
                                    "Déjà un compte ? Se connecter",
                                color = SoftGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = label, color = TextGray)
        },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBg,
            unfocusedContainerColor = SearchBg,
            disabledContainerColor = SearchBg,
            focusedBorderColor = PrimaryRed,
            unfocusedBorderColor = MutedBorder,
            cursorColor = PrimaryRed,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedLabelColor = PrimaryRed,
            unfocusedLabelColor = TextGray
        )
    )
}

@Composable
private fun AuthSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextWhite,
            fontWeight = FontWeight.Medium
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = PrimaryRed,
                uncheckedThumbColor = SoftGray,
                uncheckedTrackColor = Color(0xFF2A2A2A)
            )
        )
    }
}