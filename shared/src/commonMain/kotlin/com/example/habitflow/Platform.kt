package com.example.habitflow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform