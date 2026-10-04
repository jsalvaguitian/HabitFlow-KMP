package com.example.habitflow.data

import com.example.habitflow.model.CreateHabitRequest
import com.example.habitflow.model.Habit
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
}
