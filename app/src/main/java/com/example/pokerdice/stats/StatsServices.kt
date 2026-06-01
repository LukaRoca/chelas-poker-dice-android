package com.example.pokerdice.stats

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokerdice.domain.HandRank
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "poker_stats")
private val PLAYER_NAMES_KEY = stringSetPreferencesKey("all_player_names")

class StatsService(private val context: Context) {

    private fun gamesPlayedKey(name: String) = intPreferencesKey("${name}_played")
    private fun gamesWonKey(name: String) = intPreferencesKey("${name}_won")
    private fun handFreqKey(name: String, rank: HandRank) = intPreferencesKey("${name}_hand_${rank.name}")

    suspend fun getStats(name: String): PlayerStats {
        return context.dataStore.data.map { preferences ->
            val gamesPlayed = preferences[gamesPlayedKey(name)] ?: 0
            val gamesWon = preferences[gamesWonKey(name)] ?: 0

            val handFrequency = HandRank.entries.associateWith { rank ->
                preferences[handFreqKey(name, rank)] ?: 0
            }

            PlayerStats(name, gamesPlayed, gamesWon, handFrequency)
        }.first()
    }

    suspend fun getAllPlayerNames(): Set<String> {
        return context.dataStore.data.map { preferences ->
            preferences[PLAYER_NAMES_KEY] ?: emptySet()
        }.first()
    }

    suspend fun saveGameResult(result: GameResult) {
        val allPlayers = listOf(result.p1Name, result.p2Name).distinct()

        context.dataStore.edit { preferences ->

            val currentNames = preferences[PLAYER_NAMES_KEY] ?: emptySet()
            val newNames = currentNames + allPlayers
            preferences[PLAYER_NAMES_KEY] = newNames

            for (name in allPlayers) {
                val playedKey = gamesPlayedKey(name)
                preferences[playedKey] = (preferences[playedKey] ?: 0) + 1

                if (name == result.winnerName) {
                    val wonKey = gamesWonKey(name)
                    preferences[wonKey] = (preferences[wonKey] ?: 0) + 1
                }

                val hands = if (name == result.p1Name) result.p1Hands else result.p2Hands
                hands.forEach { (rank, count) ->
                    val freqKey = handFreqKey(name, rank)
                    preferences[freqKey] = (preferences[freqKey] ?: 0) + count
                }
            }
        }
    }
}