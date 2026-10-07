package com.example.habitflow.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateHabitCompletionRequest(
    val completed: Boolean,
)
