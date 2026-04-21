package com.discordbotmaker.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.discordbotmaker.android.ui.automod.AutoModScreen
import com.discordbotmaker.android.ui.commands.CommandBuilderScreen
import com.discordbotmaker.android.ui.console.LiveConsoleScreen
import com.discordbotmaker.android.ui.console.LiveConsoleViewModel
import com.discordbotmaker.android.ui.dashboard.BotStatus
import com.discordbotmaker.android.ui.dashboard.MainDashboardScreen
import com.discordbotmaker.android.ui.launch.BotCreationScreen

object AppRoutes {
    const val DASHBOARD       = "dashboard"
    const val LIVE_CONSOLE    = "live_console"
    const val AUTO_MOD        = "auto_mod"
    const val COMMAND_BUILDER = "command_builder"
    const val BOT_CREATION    = "bot_creation"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    consoleViewModel: LiveConsoleViewModel = LiveConsoleViewModel(),
    botStatus: BotStatus = BotStatus()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.DASHBOARD
    ) {
        composable(AppRoutes.DASHBOARD) {
            MainDashboardScreen(
                botStatus = botStatus,
                onNavigateToConsole = { navController.navigate(AppRoutes.LIVE_CONSOLE) { launchSingleTop = true } },
                onNavigateToAutoMod = { navController.navigate(AppRoutes.AUTO_MOD) { launchSingleTop = true } },
                onNavigateToCommandBuilder = { navController.navigate(AppRoutes.COMMAND_BUILDER) { launchSingleTop = true } },
                onNavigateToBotCreation = { navController.navigate(AppRoutes.BOT_CREATION) { launchSingleTop = true } }
            )
        }
        composable(AppRoutes.LIVE_CONSOLE) { LiveConsoleScreen(viewModel = consoleViewModel) }
        composable(AppRoutes.AUTO_MOD) { AutoModScreen() }
        composable(AppRoutes.COMMAND_BUILDER) { CommandBuilderScreen() }
        composable(AppRoutes.BOT_CREATION) { BotCreationScreen() }
    }
}
