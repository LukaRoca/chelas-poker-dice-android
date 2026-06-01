package com.example.pokerdice.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pokerdice.domain.AiStrategy
import com.example.pokerdice.domain.DiceFace
import com.example.pokerdice.domain.PokerDiceEvaluator
import com.example.pokerdice.domain.HandRank
import com.example.pokerdice.stats.GameResult
import com.example.pokerdice.stats.StatsService
import com.example.pokerdice.ui.matchSetup.GameMode
import com.example.pokerdice.ui.matchSetup.StartingPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DieState(
    val face: DiceFace,
    val isHeld: Boolean
)

enum class GameState {
    PLAYER_TURN,
    AI_TURN,
    ROUND_RESULT,
    GAME_OVER
}

class GameViewModel(private val statsServices: StatsService) : ViewModel() {

    var player1Name by mutableStateOf("Player 1")
    var player2Name by mutableStateOf("Player 2")
    var totalRounds by mutableIntStateOf(3)
    var gameMode by mutableStateOf(GameMode.HUMAN_VS_HUMAN)

    var currentRound by mutableIntStateOf(1)
    var currentPlayerIs1 by mutableStateOf(true)

    var gameState by mutableStateOf(GameState.PLAYER_TURN)

    var scoreP1 by mutableIntStateOf(0)
    var scoreP2 by mutableIntStateOf(0)

    val dice = mutableStateListOf<DieState>().apply {
        repeat(5) { add(DieState(DiceFace.NINE, false)) }
    }

    var rollsLeft by mutableIntStateOf(3)
    var canRoll by mutableStateOf(true)
    var turnMessage by mutableStateOf("")

    private val p1HandHistory = mutableListOf<List<DiceFace>>()
    private val p2HandHistory = mutableListOf<List<DiceFace>>()

    private var handP1: List<DiceFace>? = null
    private var handP2: List<DiceFace>? = null

    var roundWinnerMessage by mutableStateOf("")

    companion object {
        fun Factory(statsServices: StatsService): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                    return GameViewModel(statsServices) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun initializeGame(p1: String, p2: String, rounds: Int, mode: GameMode, start: StartingPlayer) {
        // Reiniciar estado completo para evitar dados de jogos anteriores
        resetGameState()

        player1Name = p1.ifBlank { "Player 1" }
        player2Name = if (mode == GameMode.HUMAN_VS_AI) "Computador" else (p2.ifBlank { "Player 2" })
        totalRounds = rounds
        gameMode = mode

        val startP1 = when(start) {
            StartingPlayer.PLAYER_1 -> true
            StartingPlayer.PLAYER_2 -> false
            StartingPlayer.RANDOM -> Random.nextBoolean()
        }
        currentPlayerIs1 = startP1

        checkTurnType()
    }

    private fun resetGameState() {
        currentRound = 1
        scoreP1 = 0
        scoreP2 = 0
        p1HandHistory.clear()
        p2HandHistory.clear()
        handP1 = null
        handP2 = null
        gameState = GameState.PLAYER_TURN
    }

    private fun checkTurnType() {
        if (!currentPlayerIs1 && gameMode == GameMode.HUMAN_VS_AI) {
            startAiTurn()
        } else {
            startHumanTurn()
        }
    }

    private fun startHumanTurn() {
        gameState = GameState.PLAYER_TURN
        resetTurnState()
        turnMessage = "Vez de ${if (currentPlayerIs1) player1Name else player2Name}. Lança os dados!"
    }

    private fun resetTurnState() {
        rollsLeft = 3
        canRoll = true
        for (i in 0 until 5) {
            dice[i] = DieState(DiceFace.NINE, false)
        }
    }

    fun toggleHold(index: Int) {
        if (gameState == GameState.PLAYER_TURN && rollsLeft < 3 && rollsLeft > 0 && canRoll) {
            val current = dice[index]
            dice[index] = current.copy(isHeld = !current.isHeld)
        }
    }

    fun rollDice() {
        if (rollsLeft <= 0 || !canRoll) return

        viewModelScope.launch {
            val indicesToRoll = if (rollsLeft == 3) {
                (0 until 5).toList()
            } else {
                dice.indices.filter { !dice[it].isHeld }
            }

            performRollAnimation(indicesToRoll)

            rollsLeft--

            val handRank = PokerDiceEvaluator.evaluate(dice.map { it.face })
            turnMessage = "${handRank.description} - $rollsLeft lançamentos restantes."

            if (gameState == GameState.PLAYER_TURN) {
                canRoll = true
            }
        }
    }

    private fun startAiTurn() {
        gameState = GameState.AI_TURN
        resetTurnState()
        turnMessage = "Vez do Computador..."
        canRoll = false

        viewModelScope.launch {
            delay(1000)

            while (rollsLeft > 0) {
                turnMessage = "Computador a lançar..."
                val indicesToRoll = if (rollsLeft == 3) {
                    (0 until 5).toList()
                } else {
                    dice.indices.filter { !dice[it].isHeld }
                }

                performRollAnimation(indicesToRoll)
                rollsLeft--

                val currentFaces = dice.map { it.face }
                val handRank = PokerDiceEvaluator.evaluate(currentFaces)
                turnMessage = "Computador tem: ${handRank.description}"

                if (rollsLeft == 0 || AiStrategy.shouldStopEarly(currentFaces)) {
                    break
                }

                delay(1500)

                turnMessage = "Computador a escolher dados..."
                val holds = AiStrategy.decideHolds(currentFaces)

                for (i in 0 until 5) {
                    dice[i] = dice[i].copy(isHeld = holds[i])
                }

                delay(1500)
            }

            delay(1000)
            finishAiTurn()
        }
    }

    private suspend fun performRollAnimation(indicesToRoll: List<Int>) {
        canRoll = false
        repeat(5) {
            for (i in indicesToRoll) {
                dice[i] = dice[i].copy(face = DiceFace.entries.random())
            }
            delay(100)
        }
        canRoll = true
    }

    private fun finishAiTurn() {
        handP2 = dice.map { it.face }
        p2HandHistory.add(handP2!!)

        // CORREÇÃO: Verifica se o P1 já jogou.
        // Se P1 ainda não jogou (handP1 == null), passa o turno para ele.
        // Se P1 já jogou, termina a ronda.
        if (handP1 == null) {
            currentPlayerIs1 = true
            checkTurnType()
        } else {
            finishRound()
        }
    }

    fun passTurn() {
        if (rollsLeft == 3) return

        val currentHand = dice.map { it.face }

        if (currentPlayerIs1) {
            handP1 = currentHand
            p1HandHistory.add(handP1!!)

            // CORREÇÃO: Lógica simétrica para Humano vs Humano
            if (handP2 == null) {
                currentPlayerIs1 = false
                checkTurnType()
            } else {
                finishRound()
            }
        } else {
            handP2 = currentHand
            p2HandHistory.add(handP2!!)

            // CORREÇÃO: Se o P2 foi o primeiro a jogar, passa ao P1
            if (handP1 == null) {
                currentPlayerIs1 = true
                checkTurnType()
            } else {
                finishRound()
            }
        }
    }

    private fun finishRound() {
        gameState = GameState.ROUND_RESULT

        // Agora é seguro usar !! porque a lógica acima garante que ambos jogaram
        val result = PokerDiceEvaluator.compare(handP1!!, handP2!!)

        if (result > 0) {
            scoreP1++
            roundWinnerMessage = "$player1Name venceu a ronda!"
        } else if (result < 0) {
            scoreP2++
            roundWinnerMessage = "$player2Name venceu a ronda!"
        } else {
            roundWinnerMessage = "Empate na ronda!"
        }
    }

    fun nextRound() {
        if (currentRound < totalRounds) {
            currentRound++


            handP1 = null
            handP2 = null

            checkTurnType()
        } else {
            gameState = GameState.GAME_OVER
            val winnerName = if (scoreP1 > scoreP2) player1Name else if (scoreP2 > scoreP1) player2Name else "Empate"
            roundWinnerMessage = "Jogo Terminado! Vencedor: $winnerName"

            saveGameStats(winnerName)
        }
    }

    private fun saveGameStats(winnerName: String) = viewModelScope.launch {
        fun countHandRanks(history: List<List<DiceFace>>): Map<HandRank, Int> {
            val counts = HandRank.entries.associateWith { 0 }.toMutableMap()
            history.forEach { hand ->
                val rank = PokerDiceEvaluator.evaluate(hand).rank
                counts[rank] = counts.getValue(rank) + 1
            }
            return counts
        }

        val p1Hands = countHandRanks(p1HandHistory)
        val p2Hands = countHandRanks(p2HandHistory)

        val result = GameResult(
            p1Name = player1Name,
            p2Name = player2Name,
            winnerName = winnerName,
            p1Hands = p1Hands,
            p2Hands = p2Hands
        )

        statsServices.saveGameResult(result)
    }
}