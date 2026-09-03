package com.discordbotmaker.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.discordbotmaker.android.ui.splash.SplashScreen
import com.discordbotmaker.android.ui.splash.OriginLoadingScreen
import com.discordbotmaker.android.ui.dashboard.MainDashboardScreen
import com.discordbotmaker.android.ui.commands.CommandBuilderScreen
import com.discordbotmaker.android.ui.automod.AutoModScreen
import com.discordbotmaker.android.ui.launch.BotCreationScreen
import com.discordbotmaker.android.ui.console.LiveConsoleScreen
import com.discordbotmaker.android.ui.music.MusicPlayerScreen
import com.discordbotmaker.android.ui.stats.StatsDashboardScreen

object NavRoutes {
    const val SPLASH = "splash"
    const val ORIGIN_LOADING = "origin_loading"
    const val MAIN_DASHBOARD = "main_dashboard"
    const val COMMAND_BUILDER = "command_builder"
    const val AUTOMOD = "automod"
    const val BOT_CREATION = "bot_creation"
    const val LIVE_CONSOLE = "live_console"
    const val MUSIC_PLAYER = "music_player"
    const val STATS_DASHBOARD = "stats_dashboard"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigate = {
                    navController.navigate(NavRoutes.ORIGIN_LOADING) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ORIGIN_LOADING) {
            OriginLoadingScreen(
                onNavigate = {
                    navController.navigate(NavRoutes.MAIN_DASHBOARD) {
                        popUpTo(NavRoutes.ORIGIN_LOADING) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.MAIN_DASHBOARD) {
            MainDashboardScreen(
                onNavigateToCommands = { navController.navigate(NavRoutes.COMMAND_BUILDER) },
                onNavigateToAutoMod = { navController.navigate(NavRoutes.AUTOMOD) },
                onNavigateToBotCreation = { navController.navigate(NavRoutes.BOT_CREATION) },
                onNavigateToConsole = { navController.navigate(NavRoutes.LIVE_CONSOLE) },
                onNavigateToMusic = { navController.navigate(NavRoutes.MUSIC_PLAYER) },
                onNavigateToStats = { navController.navigate(NavRoutes.STATS_DASHBOARD) }
            )
        }

        composable(NavRoutes.COMMAND_BUILDER) {
            CommandBuilderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AUTOMOD) {
            AutoModScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.BOT_CREATION) {
            BotCreationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.LIVE_CONSOLE) {
            LiveConsoleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MUSIC_PLAYER) {
            MusicPlayerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.STATS_DASHBOARD) {
            StatsDashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
