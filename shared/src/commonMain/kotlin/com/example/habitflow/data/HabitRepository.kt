package com.example.habitflow.data

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
}
