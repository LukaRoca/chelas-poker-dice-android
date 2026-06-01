package com.example.pokerdice.ui.matchSetup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class GameMode {
    HUMAN_VS_HUMAN,
    HUMAN_VS_AI
}

enum class StartingPlayer {
    PLAYER_1,
    PLAYER_2,
    RANDOM
}

class MatchSetupViewModel : ViewModel() {

    var player1Name by mutableStateOf("")
    var player2Name by mutableStateOf("")
    var rounds by mutableStateOf("3") // Default 3 rondas
    var gameMode by mutableStateOf(GameMode.HUMAN_VS_HUMAN)
    var startingPlayer by mutableStateOf(StartingPlayer.RANDOM)

    var roundsError by mutableStateOf<String?>(null)

    fun onStartMatchClick(onSuccess: () -> Unit) {
        val roundsInt = rounds.toIntOrNull()

        if (roundsInt == null || roundsInt <= 0) {
            roundsError = "O número de rondas deve ser positivo."
            return
        }

        if (roundsInt % 2 == 0) {
            roundsError = "O número de rondas deve ser ímpar."
            return
        }

        roundsError = null
        onSuccess()
    }
}