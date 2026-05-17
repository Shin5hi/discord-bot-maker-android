package com.discordbotmaker.android.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.discordbotmaker.android.data.DefaultBotMakerRepository
import com.discordbotmaker.android.feature.automod.AutoModRoute
import com.discordbotmaker.android.feature.console.ConsoleRoute
import com.discordbotmaker.android.feature.createbot.CreateBotRoute
import com.discordbotmaker.android.feature.home.BotHomeRoute
import com.discordbotmaker.android.feature.settings.SettingsRoute
import com.discordbotmaker.android.feature.splash.GridSplashScreen
import com.discordbotmaker.android.feature.templates.TemplatesScreen

object AppRoute {
    const val Splash = "splash"
    const val Home = "home"
    const val Templates = "templates"
    const val Settings = "settings"
    const val CreateBot = "create-bot"
    const val Console = "console"
    const val AutoMod = "automod"
}

@Composable
fun DiscordBotMakerNavHost(
    navController: NavHostController,
    container: AppContainer,
) {
    val repository = remember(container) {
        DefaultBotMakerRepository(
            settingsRepository = container.settingsRepository,
            api = container.api,
        )
    }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination
    val showBottomBar = currentDestination?.route in setOf(
        AppRoute.Home,
        AppRoute.Templates,
        AppRoute.Settings,
    )

    Scaffold(
        containerColor = Color(0xFF0F131B),
        bottomBar = {
            if (showBottomBar) {
                GridBottomBar(
                    currentRoute = currentDestination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(AppRoute.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppRoute.Splash) {
                GridSplashScreen(
                    onFinished = {
                        navController.navigate(AppRoute.Home) {
                            popUpTo(AppRoute.Splash) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(AppRoute.Home) {
                BotHomeRoute(
                    repository = repository,
                    onOpenCreate = { navController.navigate(AppRoute.CreateBot) },
                    onOpenConsole = { navController.navigate(AppRoute.Console) },
                    onOpenAutoMod = { navController.navigate(AppRoute.AutoMod) },
                )
            }
            composable(AppRoute.Templates) {
                TemplatesScreen()
            }
            composable(AppRoute.Settings) {
                SettingsRoute(
                    repository = repository,
                    onOpenSetup = { navController.navigate(AppRoute.CreateBot) },
                    onOpenConsole = { navController.navigate(AppRoute.Console) },
                    onOpenAutoMod = { navController.navigate(AppRoute.AutoMod) },
                )
            }
            composable(AppRoute.CreateBot) {
                CreateBotRoute(
                    repository = repository,
                    onRegistered = {
                        navController.navigate(AppRoute.Home) {
                            popUpTo(AppRoute.Home) { inclusive = true }
                        }
                    },
                )
            }
            composable(AppRoute.Console) {
                ConsoleRoute(repository = repository)
            }
            composable(AppRoute.AutoMod) {
                AutoModRoute(repository = repository)
            }
        }
    }
}

private data class BottomBarItem(
    val route: String,
    val label: String,
    val icon: String,
)

@Composable
private fun GridBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        BottomBarItem(AppRoute.Home, "Home", "⌂"),
        BottomBarItem(AppRoute.Templates, "Templates", "◫"),
        BottomBarItem(AppRoute.Settings, "Settings", "⚙"),
    )

    NavigationBar(
        containerColor = Color(0xFF111521),
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Text(
                        text = item.icon,
                        color = if (selected) Color(0xFF6C78FF) else Color(0xFF7E879E),
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) Color(0xFF6C78FF) else Color(0xFF7E879E),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0x1F6C78FF),
                    selectedIconColor = Color(0xFF6C78FF),
                    selectedTextColor = Color(0xFF6C78FF),
                    unselectedIconColor = Color(0xFF7E879E),
                    unselectedTextColor = Color(0xFF7E879E),
                ),
            )
        }
    }
}
