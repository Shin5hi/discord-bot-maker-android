package com.discordbotmaker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discordbotmaker.android.ui.navigation.AppNavGraph
import com.discordbotmaker.android.ui.theme.AppColors
import com.discordbotmaker.android.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.background(AppColors.Background),
                    color = AppColors.Background
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}
