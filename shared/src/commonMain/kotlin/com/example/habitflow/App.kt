package com.example.habitflow

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitflow.ui.CreateHabitScreen
import com.example.habitflow.ui.HabitsViewModel
import com.example.habitflow.ui.HomeScreen

@Composable
@Preview
fun App(viewModel: HabitsViewModel = viewModel { HabitsViewModel() }) {
    var showCreateScreen by remember { mutableStateOf(false) }

    MaterialTheme {
        if (showCreateScreen) {
            CreateHabitScreen(
                viewModel = viewModel,
                onBack = { showCreateScreen = false },
            )
        } else {
            HomeScreen(
                viewModel = viewModel,
                onCreateHabitClick = { showCreateScreen = true },
            )
        }
    }
}
