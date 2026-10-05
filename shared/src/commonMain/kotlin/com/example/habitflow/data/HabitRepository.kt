package com.example.habitflow.data

import com.example.habitflow.model.CreateHabitRequest
import com.example.habitflow.model.Habit
import com.example.habitflow.model.UpdateHabitCompletionRequest
import com.example.habitflow.model.UpdateHabitRequest
import com.example.habitflow.supabase
import io.github.jan.supabase.postgrest.from

class HabitRepository {
    suspend fun getHabits(): List<Habit> {
        return supabase
            .from("habits")
            .select()
            .decodeList<Habit>()
    }

    suspend fun createHabit(title: String, description: String?, frequency: String) {
        val request = CreateHabitRequest(
            title = title,
            description = description?.ifBlank { null },
            frequency = frequency,
        )
        supabase
            .from("habits")
            .insert(request)
    }

    suspend fun updateHabitCompletion(id: Long, completed: Boolean) {
        val request = UpdateHabitCompletionRequest(completed = completed)
        supabase
            .from("habits")
            .update(request) {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun updateHabit(id: Long, title: String, description: String?, frequency: String) {
        val request = UpdateHabitRequest(
            title = title,
            description = description?.ifBlank { null },
            frequency = frequency,
        )
        supabase
            .from("habits")
            .update(request) {
                filter {
                    eq("id", id)
                }
            }
    }
}
