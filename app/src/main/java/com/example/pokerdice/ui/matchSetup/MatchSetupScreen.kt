package com.example.pokerdice.ui.matchSetup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MatchSetupScreen(
    onStartGame: (String, String, String, GameMode, StartingPlayer) -> Unit,
    onCancel: () -> Unit,
    viewModel: MatchSetupViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Configuração do Jogo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Modo de Jogo",
            style = MaterialTheme.typography.titleMedium
        )
        RadioGroup(
            options = listOf("Humano vs Humano", "Humano vs IA"),
            selectedIndex = if (viewModel.gameMode == GameMode.HUMAN_VS_HUMAN) 0 else 1,
            onOptionSelected = { index ->
                viewModel.gameMode = if (index == 0) GameMode.HUMAN_VS_HUMAN else GameMode.HUMAN_VS_AI
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.player1Name,
            onValueChange = { viewModel.player1Name = it },
            label = { Text("Nome do Jogador 1") },
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.gameMode == GameMode.HUMAN_VS_HUMAN) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.player2Name,
                onValueChange = { viewModel.player2Name = it },
                label = { Text("Nome do Jogador 2") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.rounds,
            onValueChange = { viewModel.rounds = it },
            label = { Text("Número de Rondas (Ímpar)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = viewModel.roundsError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.roundsError != null) {
            Text(
                text = viewModel.roundsError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jogador Inicial",
            style = MaterialTheme.typography.titleMedium
        )
        RadioGroup(
            options = listOf("Jogador 1", "Jogador 2", "Aleatório"),
            selectedIndex = when(viewModel.startingPlayer) {
                StartingPlayer.PLAYER_1 -> 0
                StartingPlayer.PLAYER_2 -> 1
                StartingPlayer.RANDOM -> 2
            },
            onOptionSelected = { index ->
                viewModel.startingPlayer = when(index) {
                    0 -> StartingPlayer.PLAYER_1
                    1 -> StartingPlayer.PLAYER_2
                    else -> StartingPlayer.RANDOM
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
            Button(onClick = {
                viewModel.onStartMatchClick {
                    onStartGame(
                        viewModel.player1Name,
                        viewModel.player2Name,
                        viewModel.rounds,
                        viewModel.gameMode,
                        viewModel.startingPlayer
                    )
                }
            }) {
                Text("Iniciar Jogo")
            }
        }
    }
}

@Composable
fun RadioGroup(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column {
        options.forEachIndexed { index, text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (index == selectedIndex),
                        onClick = { onOptionSelected(index) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (index == selectedIndex),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text)
            }
        }
    }
}