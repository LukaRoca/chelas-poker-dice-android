package com.example.pokerdice.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokerdice.domain.DiceFace
import com.example.pokerdice.ui.matchSetup.GameMode
import com.example.pokerdice.ui.matchSetup.StartingPlayer

@Composable
fun GameScreen(
    player1Name: String,
    player2Name: String,
    rounds: Int,
    gameMode: GameMode,
    startingPlayer: StartingPlayer,
    onEndGame: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.initializeGame(player1Name, player2Name, rounds, gameMode, startingPlayer)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ScoreHeader(
            p1Name = viewModel.player1Name,
            p2Name = viewModel.player2Name,
            scoreP1 = viewModel.scoreP1,
            scoreP2 = viewModel.scoreP2,
            round = viewModel.currentRound,
            totalRounds = viewModel.totalRounds
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.gameState == GameState.PLAYER_TURN || viewModel.gameState == GameState.AI_TURN) {

            Text(
                text = viewModel.turnMessage,
                style = MaterialTheme.typography.headlineSmall,
                color = if (viewModel.gameState == GameState.AI_TURN)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                viewModel.dice.forEachIndexed { index, dieState ->
                    DiceView(
                        face = dieState.face,
                        isHeld = dieState.isHeld,
                        onClick = {
                            if (viewModel.gameState == GameState.PLAYER_TURN && viewModel.canRoll) {
                                viewModel.toggleHold(index)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (viewModel.gameState == GameState.AI_TURN) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.rollDice() },
                        enabled = viewModel.gameState == GameState.PLAYER_TURN && viewModel.canRoll && viewModel.rollsLeft > 0
                    ) {
                        Text("Lançar (${viewModel.rollsLeft})")
                    }

                    Button(
                        onClick = { viewModel.passTurn() },
                        enabled = viewModel.gameState == GameState.PLAYER_TURN && viewModel.canRoll && viewModel.rollsLeft < 3,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Terminar Turno")
                    }
                }
            }

        } else if (viewModel.gameState == GameState.ROUND_RESULT) {
            RoundResultView(
                message = viewModel.roundWinnerMessage,
                onNextRound = { viewModel.nextRound() }
            )
        } else {
            GameOverView(
                message = viewModel.roundWinnerMessage,
                onExit = onEndGame
            )
        }
    }
}

@Composable
fun ScoreHeader(p1Name: String, p2Name: String, scoreP1: Int, scoreP2: Int, round: Int, totalRounds: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = p1Name, fontWeight = FontWeight.Bold)
            Text(text = scoreP1.toString(), fontSize = 24.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Ronda", style = MaterialTheme.typography.labelMedium)
            Text(text = "$round / $totalRounds", fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = p2Name, fontWeight = FontWeight.Bold)
            Text(text = scoreP2.toString(), fontSize = 24.sp)
        }
    }
}

@Composable
fun DiceView(face: DiceFace, isHeld: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isHeld) MaterialTheme.colorScheme.primaryContainer else Color.White
    val borderColor = if (isHeld) MaterialTheme.colorScheme.primary else Color.Black

    Card(
        modifier = Modifier
            .size(65.dp)
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = face.label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RoundResultView(message: String, onNextRound: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Resultado da Ronda", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNextRound) {
            Text("Próxima Ronda")
        }
    }
}

@Composable
fun GameOverView(message: String, onExit: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Fim de Jogo", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onExit) {
            Text("Voltar ao Menu")
        }
    }
}