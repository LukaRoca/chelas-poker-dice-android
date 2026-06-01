package com.example.pokerdice.ui.title

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val TITLE_ROUTE = "title"

fun NavGraphBuilder.titleScreen(
    onStartMatch: () -> Unit,
    onStatistics: () -> Unit,
    onAbout: () -> Unit
) {
    composable(TITLE_ROUTE) {
        TitleScreen(
            onStartMatch = onStartMatch,
            onStatistics = onStatistics,
            onAbout = onAbout
        )
    }
}