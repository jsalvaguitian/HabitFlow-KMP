package com.example.habitflow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitflow.model.Habit
import com.example.habitflow.ui.CreateHabitScreen
import com.example.habitflow.ui.HabitsViewModel
import com.example.habitflow.ui.HomeScreen
import com.example.habitflow.ui.theme.HabitFlowTheme

@Composable
@Preview
fun App(viewModel: HabitsViewModel = viewModel { HabitsViewModel() }) {
    var showCreateOrEditScreen by remember { mutableStateOf(false) }
    var selectedHabitForEdit by remember { mutableStateOf<Habit?>(null) }

    HabitFlowTheme {
        if (showCreateOrEditScreen) {
            CreateHabitScreen(
                viewModel = viewModel,
                habitToEdit = selectedHabitForEdit,
                onBack = {
                    showCreateOrEditScreen = false
                    selectedHabitForEdit = null
                },
            )
        } else {
            HomeScreen(
                viewModel = viewModel,
                onCreateHabitClick = {
                    selectedHabitForEdit = null
                    showCreateOrEditScreen = true
                },
                onEditHabitClick = { habit ->
                    selectedHabitForEdit = habit
                    showCreateOrEditScreen = true
                },
            )
        }
    }
}
