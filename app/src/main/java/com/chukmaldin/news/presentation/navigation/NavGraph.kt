package com.chukmaldin.news.presentation.navigation

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chukmaldin.news.domain.entity.Settings
import com.chukmaldin.news.presentation.screen.settings.SettingsScreen
import com.chukmaldin.news.presentation.screen.subscriptions.SubscriptionsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Subscriptions.route
    ) {
        composable(Screen.Subscriptions.route) {

            SubscriptionsScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Subscriptions: Screen("subscriptions")

    data object Settings: Screen("settings")
}