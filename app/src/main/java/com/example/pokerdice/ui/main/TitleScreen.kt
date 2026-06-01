package com.example.pokerdice.ui.title

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokerdice.ui.theme.PokerDiceTheme

@Composable
fun TitleScreen(
    onStartMatch: () -> Unit,
    onStatistics: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chelas Poker Dice")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStartMatch) {
            Text(text = "Start Match")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStatistics) {
            Text(text = "Statistics")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAbout) {
            Text(text = "About")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleScreenPreview() {
    PokerDiceTheme {
        TitleScreen({}, {}, {})
    }
}