package com.example.pokerdice.ui.matchSetup

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MATCH_SETUP_ROUTE = "match_setup"

fun NavController.navigateToMatchSetup() {
    navigate(MATCH_SETUP_ROUTE)
}

fun NavGraphBuilder.matchSetupScreen(
    onStartGame: (String, String, String, GameMode, StartingPlayer) -> Unit,
    onCancel: () -> Unit
) {
    composable(MATCH_SETUP_ROUTE) {
        MatchSetupScreen(
            onStartGame = onStartGame,
            onCancel = onCancel
        )
    }
}