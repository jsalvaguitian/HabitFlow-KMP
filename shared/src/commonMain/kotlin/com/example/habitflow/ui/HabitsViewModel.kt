package com.example.habitflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.data.HabitRepository
import com.example.habitflow.data.SupabaseHabitRepository
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

sealed interface CreateHabitUiState {
    data object Idle : CreateHabitUiState
    data object Loading : CreateHabitUiState
    data object Success : CreateHabitUiState
    data class ValidationError(
        val titleError: String? = null,
        val frequencyError: String? = null,
    ) : CreateHabitUiState
    data class Error(val message: String) : CreateHabitUiState
}

class HabitsViewModel(
    private val repository: HabitRepository = SupabaseHabitRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitsUiState>(HabitsUiState.Loading)
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    private val _createUiState = MutableStateFlow<CreateHabitUiState>(CreateHabitUiState.Idle)
    val createUiState: StateFlow<CreateHabitUiState> = _createUiState.asStateFlow()

    private val _updatingHabitIds = MutableStateFlow<Set<Long>>(emptySet())
    val updatingHabitIds: StateFlow<Set<Long>> = _updatingHabitIds.asStateFlow()

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

    fun toggleHabitCompletion(habit: Habit) {
        val targetId = habit.id
        if (_updatingHabitIds.value.contains(targetId)) return

        val newCompleted = !habit.completed
        _updatingHabitIds.value = _updatingHabitIds.value + targetId

        viewModelScope.launch {
            try {
                repository.updateHabitCompletion(targetId, newCompleted)
                val currentState = _uiState.value
                if (currentState is HabitsUiState.Success) {
                    _uiState.value = HabitsUiState.Success(
                        currentState.habits.map { currentHabit ->
                            if (currentHabit.id == targetId) {
                                currentHabit.copy(completed = newCompleted)
                            } else {
                                currentHabit
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                // Keep current state
            } finally {
                _updatingHabitIds.value = _updatingHabitIds.value - targetId
            }
        }
    }

    fun createHabit(title: String, description: String?, frequency: String) {
        val trimmedTitle = title.trim()
        val trimmedFrequency = frequency.trim()

        val titleError = if (trimmedTitle.isEmpty()) "El título es obligatorio" else null
        val frequencyError = if (trimmedFrequency.isEmpty()) "La frecuencia es obligatoria" else null

        if (titleError != null || frequencyError != null) {
            _createUiState.value = CreateHabitUiState.ValidationError(
                titleError = titleError,
                frequencyError = frequencyError,
            )
            return
        }

        viewModelScope.launch {
            _createUiState.value = CreateHabitUiState.Loading
            try {
                repository.createHabit(
                    title = trimmedTitle,
                    description = description?.trim(),
                    frequency = trimmedFrequency,
                )
                _createUiState.value = CreateHabitUiState.Success
                loadHabits()
            } catch (e: Exception) {
                _createUiState.value = CreateHabitUiState.Error(
                    e.message ?: "Error al crear el hábito en Supabase",
                )
            }
        }
    }

    fun updateHabit(id: Long, title: String, description: String?, frequency: String) {
        val trimmedTitle = title.trim()
        val trimmedFrequency = frequency.trim()

        val titleError = if (trimmedTitle.isEmpty()) "El título es obligatorio" else null
        val frequencyError = if (trimmedFrequency.isEmpty()) "La frecuencia es obligatoria" else null

        if (titleError != null || frequencyError != null) {
            _createUiState.value = CreateHabitUiState.ValidationError(
                titleError = titleError,
                frequencyError = frequencyError,
            )
            return
        }

        viewModelScope.launch {
            _createUiState.value = CreateHabitUiState.Loading
            try {
                repository.updateHabit(
                    id = id,
                    title = trimmedTitle,
                    description = description?.trim(),
                    frequency = trimmedFrequency,
                )
                _createUiState.value = CreateHabitUiState.Success
                loadHabits()
            } catch (e: Exception) {
                _createUiState.value = CreateHabitUiState.Error(
                    e.message ?: "Error al actualizar el hábito en Supabase",
                )
            }
        }
    }

    fun resetCreateState() {
        _createUiState.value = CreateHabitUiState.Idle
    }

    fun deleteHabit(id: Long) {
        if (_updatingHabitIds.value.contains(id)) return
        _updatingHabitIds.value = _updatingHabitIds.value + id

        viewModelScope.launch {
            try {
                repository.deleteHabit(id)
                loadHabits()
            } catch (e: Exception) {
                _uiState.value = HabitsUiState.Error(
                    e.message ?: "Error al eliminar el hábito"
                )
            } finally {
                _updatingHabitIds.value = _updatingHabitIds.value - id
            }
        }
    }
}
