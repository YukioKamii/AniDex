package com.example.anidex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.anidex.screen.HomeScreen
import com.example.anidex.screen.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("home") }

            when (currentScreen) {
                "home" -> HomeScreen(
                    onNavigateToProfile = {
                        currentScreen = "profile"
                    }
                )

                "profile" -> ProfileScreen()
            }
        }
    }
}