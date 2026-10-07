package com.example.habitflow.data

import com.example.habitflow.model.Habit
import com.example.habitflow.model.dto.CreateHabitRequest
import com.example.habitflow.model.dto.UpdateHabitCompletionRequest
import com.example.habitflow.model.dto.UpdateHabitRequest
import com.example.habitflow.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class SupabaseHabitRepository : HabitRepository {
    override suspend fun getHabits(): List<Habit> {
        return supabase
            .from("habits")
            .select {
                order("id", order = Order.ASCENDING)
            }
            .decodeList<Habit>()
    }

    override suspend fun createHabit(title: String, description: String?, frequency: String) {
        val request = CreateHabitRequest(
            title = title,
            description = description?.ifBlank { null },
            frequency = frequency,
        )
        supabase
            .from("habits")
            .insert(request)
    }

    override suspend fun updateHabitCompletion(id: Long, completed: Boolean) {
        val request = UpdateHabitCompletionRequest(completed = completed)
        supabase
            .from("habits")
            .update(request) {
                filter {
                    eq("id", id)
                }
            }
    }

    override suspend fun updateHabit(id: Long, title: String, description: String?, frequency: String) {
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

    override suspend fun deleteHabit(id: Long) {
        supabase
            .from("habits")
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}
