package com.example.habitflow.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateHabitRequest(
    val title: String,
    val description: String? = null,
    val frequency: String,
)
