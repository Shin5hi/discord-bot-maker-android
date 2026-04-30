package com.discordbotmaker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.discordbotmaker.android.ui.console.LiveConsoleViewModel
import com.discordbotmaker.android.ui.dashboard.BotStatus
import com.discordbotmaker.android.ui.navigation.AppNavGraph
import com.discordbotmaker.android.ui.theme.NeonBotMakerTheme

/**
 * Main Activity - Entry point for the Discord Bot Maker Android app
 *
 * This app allows users to build, deploy, and manage Discord bots directly from their Android device.
 * Features include:
 * - Live Console for real-time bot log streaming
 * - AI-powered AutoMod configuration
 * - Custom command builder
 * - Bot deployment and management
 */
class MainActivity : ComponentActivity() {

    private val consoleViewModel = LiveConsoleViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NeonBotMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    // Sample bot status - in a real app, this would come from a ViewModel
                    val botStatus = BotStatus(
                        isOnline = false,
                        botName = "MyDiscordBot",
                        serverCount = 0,
                        memberCount = 0,
                        uptimeFormatted = "—",
                        ping = 0
                    )

                    AppNavGraph(
                        navController = navController,
                        consoleViewModel = consoleViewModel,
                        botStatus = botStatus
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources if needed
    }
}
