package com.example.habitflow.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    val id: Long,
    val title: String,
    val description: String? = null,
    val frequency: String,
    val completed: Boolean,
    @SerialName("created_at")
    val createdAt: String,
)
