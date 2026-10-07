package com.example.habitflow.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateHabitCompletionRequest(
    val completed: Boolean,
)
