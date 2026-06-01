package com.example.pokerdice.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pokerdice.domain.HandRank
import com.example.pokerdice.stats.PlayerStats
import com.example.pokerdice.stats.StatsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StatsUiState(
    val playerStats: List<PlayerStats> = emptyList(),
    val uniqueHandRanks: List<HandRank> = HandRank.entries,
    val isLoading: Boolean = true
)

class StatsViewModel(private val service: StatsService) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val loadedStats = mutableListOf<PlayerStats>()

            val allNames = service.getAllPlayerNames()

            for (name in allNames.sorted()) {
                val stats = service.getStats(name)
                loadedStats.add(stats)
            }

            _uiState.value = _uiState.value.copy(
                playerStats = loadedStats.filter { it.gamesPlayed > 0 },
                isLoading = false
            )
        }
    }

    companion object {
        fun Factory(repository: StatsService): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                    return StatsViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}