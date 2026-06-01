package com.example.pokerdice.ui.stats

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.pokerdice.stats.StatsService

const val STATS_ROUTE = "stats"

fun NavController.navigateToStats() {
    navigate(STATS_ROUTE)
}

fun NavGraphBuilder.statsScreen(
    statsService: StatsService,
    onBack: () -> Unit
) {
    composable(STATS_ROUTE) {
        val viewModel = viewModel<StatsViewModel>(
            factory = StatsViewModel.Factory(statsService)
        )
        StatsScreen(viewModel = viewModel, onBack = onBack)
    }
}