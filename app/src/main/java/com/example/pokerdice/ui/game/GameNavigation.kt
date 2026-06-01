package com.example.pokerdice.ui.game

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokerdice.ui.matchSetup.GameMode
import com.example.pokerdice.ui.matchSetup.StartingPlayer

const val GAME_ROUTE = "game_route"
const val ARG_P1 = "p1"
const val ARG_P2 = "p2"
const val ARG_ROUNDS = "rounds"
const val ARG_MODE = "mode"
const val ARG_START = "start"

fun NavController.navigateToGame(
    p1: String,
    p2: String,
    rounds: String,
    mode: GameMode,
    start: StartingPlayer
) {
    val modeStr = mode.name
    val startStr = start.name
    val p2Safe = p2.ifBlank { "Player 2" }

    navigate("$GAME_ROUTE/$p1/$p2Safe/$rounds/$modeStr/$startStr")
}

fun NavGraphBuilder.gameScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onEndGame: () -> Unit
) {
    composable(
        route = "$GAME_ROUTE/{$ARG_P1}/{$ARG_P2}/{$ARG_ROUNDS}/{$ARG_MODE}/{$ARG_START}",
        arguments = listOf(
            navArgument(ARG_P1) { type = NavType.StringType },
            navArgument(ARG_P2) { type = NavType.StringType },
            navArgument(ARG_ROUNDS) { type = NavType.IntType },
            navArgument(ARG_MODE) { type = NavType.StringType },
            navArgument(ARG_START) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val p1 = backStackEntry.arguments?.getString(ARG_P1) ?: ""
        val p2 = backStackEntry.arguments?.getString(ARG_P2) ?: ""
        val rounds = backStackEntry.arguments?.getInt(ARG_ROUNDS) ?: 3
        val modeStr = backStackEntry.arguments?.getString(ARG_MODE) ?: GameMode.HUMAN_VS_HUMAN.name
        val startStr = backStackEntry.arguments?.getString(ARG_START) ?: StartingPlayer.RANDOM.name

        val mode = GameMode.valueOf(modeStr)
        val start = StartingPlayer.valueOf(startStr)

        GameScreen(
            player1Name = p1,
            player2Name = p2,
            rounds = rounds,
            gameMode = mode,
            startingPlayer = start,
            onEndGame = onEndGame,
            viewModel = viewModel(factory = viewModelFactory)
        )
    }
}