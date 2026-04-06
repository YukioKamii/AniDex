package com.example.anidex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.anidex.navigation.AniDexNavHost
import com.example.anidex.ui.theme.AniDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AniDexTheme {
                val navController = rememberNavController()
                AniDexNavHost(navController = navController)
            }
        }
    }
}