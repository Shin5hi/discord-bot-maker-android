package com.discordbotmaker.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.discordbotmaker.android.ui.automod.AutoModScreen
import com.discordbotmaker.android.ui.commands.CommandBuilderScreen
import com.discordbotmaker.android.ui.console.LiveConsoleScreen
import com.discordbotmaker.android.ui.console.LiveConsoleViewModel
import com.discordbotmaker.android.ui.launch.BotCreationScreen
import com.discordbotmaker.android.ui.theme.AppColors

private enum class AppRoute(
    val route: String,
    val title: String,
    val inBottomBar: Boolean = false
) {
    Dashboard("dashboard", "Dashboard", true),
    Console("console", "Console"),
    AutoMod("automod", "AutoMod"),
    Commands("commands", "Commands"),
    BotCreation("bot_creation", "Bot Launch"),
    Settings("settings", "Settings", true)
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val consoleViewModel: LiveConsoleViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoute.Dashboard.route
    val bottomBarItems = AppRoute.entries.filter { it.inBottomBar }

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            NavigationBar(containerColor = AppColors.Surface) {
                bottomBarItems.forEach { item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(text = if (item == AppRoute.Dashboard) "⌂" else "⚙") },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(AppRoute.Dashboard.route) {
                DashboardScreen(
                    currentRoute = currentRoute,
                    onNavigate = { route -> navController.navigate(route.route) }
                )
            }
            composable(AppRoute.Console.route) {
                LiveConsoleScreen(viewModel = consoleViewModel)
            }
            composable(AppRoute.AutoMod.route) {
                AutoModScreen()
            }
            composable(AppRoute.Commands.route) {
                CommandBuilderScreen()
            }
            composable(AppRoute.BotCreation.route) {
                BotCreationScreen()
            }
            composable(AppRoute.Settings.route) {
                PlaceholderScreen(
                    title = "Settings",
                    message = "Configura aquí futuras opciones globales de la app."
                )
            }
        }
    }
}

@Composable
private fun DashboardScreen(
    currentRoute: String,
    onNavigate: (AppRoute) -> Unit
) {
    val shortcuts = listOf(
        AppRoute.Console to "Ver logs en tiempo real",
        AppRoute.AutoMod to "Configurar moderación automática",
        AppRoute.Commands to "Administrar comandos del bot",
        AppRoute.BotCreation to "Conectar y desplegar un bot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Discord Bot Maker",
            style = MaterialTheme.typography.headlineMedium,
            color = AppColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Navegación principal restaurada. Selecciona un módulo para continuar.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        shortcuts.forEach { (route, description) ->
            NavigationCard(
                title = route.title,
                description = description,
                selected = currentRoute == route.route,
                onClick = { onNavigate(route) }
            )
        }
    }
}

@Composable
private fun NavigationCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AppColors.PrimarySubtle else AppColors.Surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.PrimarySubtle)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "▸",
                    color = AppColors.Primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = AppColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = AppColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
