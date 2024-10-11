package com.phoenix.newsapp.feature_news.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phoenix.newsapp.feature_news.presentation.main_screen.MainScreen
import com.phoenix.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Home
                ) {
                    composable<Home> {
                        MainScreen()
                    }
//                    composable<T>(
//                        enterTransition = { slideInVertically(initialOffsetY = { it }) },
//                        exitTransition = { slideOutVertically(targetOffsetY = { it }) },
//                        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
//                        popExitTransition = { slideOutVertically(targetOffsetY = { it }) }
//                    ) {
//                        Screen()
//                    }
                }
            }
        }
    }

    @Serializable
    object Home

}