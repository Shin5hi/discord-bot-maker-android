package com.discordbotmaker.android.ui.splash

import androidx.compose.runtime.Composable
import com.discordbotmaker.android.ui.theme.AppColors

// ─── SplashScreen — Delegates to Grid Origin Loading Screen ──────────────────
//
// This file now delegates to OriginLoadingScreen, the official Grid Origin
// branded loading experience. The SplashScreen composable name is preserved
// for navigation compatibility with AppNavigation.kt.
//
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit = {}
) {
    OriginLoadingScreen(
        onLoadingComplete = onSplashComplete
    )
}
