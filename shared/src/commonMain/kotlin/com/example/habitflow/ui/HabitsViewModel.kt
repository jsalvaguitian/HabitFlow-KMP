package com.example.habitflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.data.HabitRepository
import com.example.habitflow.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HabitsUiState {
    data object Loading : HabitsUiState
    data class Success(val habits: List<Habit>) : HabitsUiState
    data class Error(val message: String) : HabitsUiState
}

class HabitsViewModel(
    private val repository: HabitRepository = HabitRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitsUiState>(HabitsUiState.Loading)
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    fun loadHabits() {
        viewModelScope.launch {
            _uiState.value = HabitsUiState.Loading
            try {
                val habits = repository.getHabits()
                _uiState.value = HabitsUiState.Success(habits)
            } catch (e: Exception) {
                _uiState.value = HabitsUiState.Error(e.message ?: "Error al cargar hábitos")
            }
        }
    }
}
