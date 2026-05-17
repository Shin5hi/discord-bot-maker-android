package com.discordbotmaker.android.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.discordbotmaker.android.feature.orion.OrionAssistantOverlay
import com.discordbotmaker.android.ui.theme.DiscordBotMakerTheme

@Composable
fun DiscordBotMakerApp() {
    val context = LocalContext.current
    val container = remember(context) { AppContainer(context.applicationContext) }
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    DiscordBotMakerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DiscordBotMakerNavHost(
                navController = navController,
                container = container,
            )

            if (currentRoute != AppRoute.Splash) {
                OrionAssistantOverlay(
                    currentRoute = currentRoute,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 20.dp),
                )
            }
        }
    }
}
