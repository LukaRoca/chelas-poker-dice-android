package com.example.pokerdice.stats

import com.example.pokerdice.domain.HandRank

data class PlayerStats(
    val playerName: String,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val handFrequency: Map<HandRank, Int> = HandRank.entries.associateWith { 0 }
) {
    val winRatio: Float
        get() = if (gamesPlayed > 0) (gamesWon.toFloat() / gamesPlayed) * 100 else 0f
}

data class GameResult(
    val p1Name: String,
    val p2Name: String,
    val winnerName: String,
    val p1Hands: Map<HandRank, Int>,
    val p2Hands: Map<HandRank, Int>
)