package com.example.habitflow.fakes

import com.example.habitflow.data.HabitRepository
import com.example.habitflow.model.Habit

class FakeHabitRepository : HabitRepository {

    val habits: MutableList<Habit> = mutableListOf()
    var shouldReturnError = false
    var errorMessage = "Error simulado en el repositorio"

    var getHabitsCallCount = 0
    var createHabitCallCount = 0
    var updateHabitCallCount = 0
    var updateCompletionCallCount = 0
    var deleteHabitCallCount = 0

    override suspend fun getHabits(): List<Habit> {
        getHabitsCallCount++
        if (shouldReturnError) {
            throw Exception(errorMessage)
        }
        return habits.toList()
    }

    override suspend fun createHabit(title: String, description: String?, frequency: String) {
        createHabitCallCount++
        if (shouldReturnError) {
            throw Exception(errorMessage)
        }
        val newId = (habits.maxOfOrNull { it.id } ?: 0L) + 1L
        val newHabit = Habit(
            id = newId,
            title = title,
            description = description,
            frequency = frequency,
            completed = false,
            createdAt = "2026-10-06T12:00:00Z",
        )
        habits.add(newHabit)
    }

    override suspend fun updateHabitCompletion(id: Long, completed: Boolean) {
        updateCompletionCallCount++
        if (shouldReturnError) {
            throw Exception(errorMessage)
        }
        val index = habits.indexOfFirst { it.id == id }
        if (index != -1) {
            habits[index] = habits[index].copy(completed = completed)
        }
    }

    override suspend fun updateHabit(id: Long, title: String, description: String?, frequency: String) {
        updateHabitCallCount++
        if (shouldReturnError) {
            throw Exception(errorMessage)
        }
        val index = habits.indexOfFirst { it.id == id }
        if (index != -1) {
            habits[index] = habits[index].copy(
                title = title,
                description = description,
                frequency = frequency,
            )
        }
    }

    override suspend fun deleteHabit(id: Long) {
        deleteHabitCallCount++
        if (shouldReturnError) {
            throw Exception(errorMessage)
        }
        habits.removeAll { it.id == id }
    }
}
