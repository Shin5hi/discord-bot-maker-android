package com.discordbotmaker.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.discordbotmaker.android.ui.automod.AutoModScreen
import com.discordbotmaker.android.ui.commands.CommandBuilderScreen
import com.discordbotmaker.android.ui.components.OrionBubble
import com.discordbotmaker.android.ui.console.LiveConsoleScreen
import com.discordbotmaker.android.ui.console.LiveConsoleViewModel
import com.discordbotmaker.android.ui.dashboard.BotStatus
import com.discordbotmaker.android.ui.dashboard.GridBottomNavBar
import com.discordbotmaker.android.ui.dashboard.MainDashboardScreen
import com.discordbotmaker.android.ui.launch.BotCreationScreen
import com.discordbotmaker.android.ui.library.ToolLibraryScreen
import com.discordbotmaker.android.ui.splash.SplashScreen
import com.discordbotmaker.android.ui.splash.OriginLoadingScreen
import com.discordbotmaker.android.ui.doubt.DoubtAssistantScreen
import com.discordbotmaker.android.ui.theme.AppColors

object AppRoutes {
    const val SPLASH               = "splash"
    const val GRID_ORIGIN_LOADING  = "grid_origin_loading"
    const val DASHBOARD            = "dashboard"
    const val TOOL_LIBRARY         = "tool_library"
    const val SETTINGS             = "settings"
    const val LIVE_CONSOLE         = "live_console"
    const val AUTO_MOD             = "auto_mod"
    const val COMMAND_BUILDER      = "command_builder"
    const val BOT_CREATION         = "bot_creation"
    const val DOUBT_ASSISTANT      = "doubt_assistant"
}

private val bottomNavRoutes = setOf(
    AppRoutes.DASHBOARD,
    AppRoutes.TOOL_LIBRARY,
    AppRoutes.SETTINGS
)

// Screens where the Orion FAB should NOT appear
private val hideOrionRoutes = setOf(
    AppRoutes.SPLASH,
    AppRoutes.GRID_ORIGIN_LOADING,
    AppRoutes.DOUBT_ASSISTANT
)

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    consoleViewModel: LiveConsoleViewModel = LiveConsoleViewModel(),
    botStatus: BotStatus = BotStatus()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoutes.SPLASH
    val showBottomNav = currentRoute in bottomNavRoutes
    val showOrionBubble = currentRoute !in hideOrionRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                GridBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(AppRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = AppRoutes.SPLASH,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(AppRoutes.SPLASH) {
                    SplashScreen(
                        onSplashComplete = {
                            navController.navigate(AppRoutes.DASHBOARD) {
                                popUpTo(AppRoutes.SPLASH) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // Grid Origin Loading --- the branded animated loading screen
                // Accessible from the launch flow; navigates to Dashboard on completion.
                composable(AppRoutes.GRID_ORIGIN_LOADING) {
                    OriginLoadingScreen(
                        onLoadingComplete = {
                            navController.navigate(AppRoutes.DASHBOARD) {
                                popUpTo(AppRoutes.GRID_ORIGIN_LOADING) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(AppRoutes.DASHBOARD) {
                    MainDashboardScreen(
                        botStatus = botStatus,
                        onNavigateToConsole = {
                            navController.navigate(AppRoutes.LIVE_CONSOLE) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToAutoMod = {
                            navController.navigate(AppRoutes.AUTO_MOD) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToCommandBuilder = {
                            navController.navigate(AppRoutes.COMMAND_BUILDER) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToBotCreation = {
                            navController.navigate(AppRoutes.BOT_CREATION) {
                                launchSingleTop = true
                            }
                        },
                        onLoginWithDiscord = {}
                    )
                }

                composable(AppRoutes.TOOL_LIBRARY) {
                    ToolLibraryScreen(
                        onToolSelected = { toolName ->
                            when (toolName) {
                                "Asistente Orion" -> navController.navigate(AppRoutes.DOUBT_ASSISTANT) {
                                    launchSingleTop = true
                                }
                                else -> { }
                            }
                        }
                    )
                }

                composable(AppRoutes.SETTINGS) {
                    SettingsPlaceholderScreen()
                }

                composable(AppRoutes.LIVE_CONSOLE) {
                    LiveConsoleScreen(viewModel = consoleViewModel)
                }

                composable(AppRoutes.AUTO_MOD) {
                    AutoModScreen()
                }

                composable(AppRoutes.COMMAND_BUILDER) {
                    CommandBuilderScreen()
                }

                composable(AppRoutes.BOT_CREATION) {
                    BotCreationScreen()
                }

                composable(AppRoutes.DOUBT_ASSISTANT) {
                    DoubtAssistantScreen()
                }
            }

            // -- Orion Floating Bubble --- Global overlay --
            if (showOrionBubble) {
                OrionBubble(
                    onClick = {
                        navController.navigate(AppRoutes.DOUBT_ASSISTANT) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsPlaceholderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "⚙", fontSize = 48.sp)
            Text(
                text = "Settings",
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Coming soon",
                color = AppColors.TextMuted,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
