package com.example.pokerdice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.pokerdice.stats.StatsService
import com.example.pokerdice.ui.about.aboutScreen
import com.example.pokerdice.ui.about.navigateToAbout
import com.example.pokerdice.ui.game.GameViewModel
import com.example.pokerdice.ui.game.gameScreen
import com.example.pokerdice.ui.game.navigateToGame
import com.example.pokerdice.ui.matchSetup.matchSetupScreen
import com.example.pokerdice.ui.matchSetup.navigateToMatchSetup
import com.example.pokerdice.ui.stats.StatsViewModel
import com.example.pokerdice.ui.stats.navigateToStats
import com.example.pokerdice.ui.stats.statsScreen
import com.example.pokerdice.ui.theme.PokerDiceTheme
import com.example.pokerdice.ui.title.TITLE_ROUTE
import com.example.pokerdice.ui.title.titleScreen

class PokerDiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val statsRepository = StatsService(applicationContext)

        setContent {
            PokerDiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = TITLE_ROUTE,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        titleScreen(
                            onStartMatch = { navController.navigateToMatchSetup() },
                            onStatistics = { navController.navigateToStats() },
                            onAbout = { navController.navigateToAbout() }
                        )

                        aboutScreen(
                            onBack = { navController.popBackStack() }
                        )

                        statsScreen(
                            statsService = statsRepository,
                            onBack = { navController.popBackStack() }
                        )

                        matchSetupScreen(
                            onStartGame = { p1, p2, rounds, mode, start ->
                                navController.navigateToGame(p1, p2, rounds, mode, start)
                            },
                            onCancel = { navController.popBackStack() }
                        )

                        gameScreen(
                            viewModelFactory = GameViewModel.Factory(statsRepository),
                            onEndGame = {
                                navController.popBackStack(TITLE_ROUTE, false)
                            }
                        )
                    }
                }
            }
        }
    }
}