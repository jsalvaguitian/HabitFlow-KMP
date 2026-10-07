package com.example.habitflow.data

import com.example.habitflow.model.Habit

interface HabitRepository {
    suspend fun getHabits(): List<Habit>
    suspend fun createHabit(title: String, description: String?, frequency: String)
    suspend fun updateHabitCompletion(id: Long, completed: Boolean)
    suspend fun updateHabit(id: Long, title: String, description: String?, frequency: String)
    suspend fun deleteHabit(id: Long)
}
