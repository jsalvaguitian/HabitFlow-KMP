package com.example.habitflow.ui

import com.example.habitflow.fakes.FakeHabitRepository
import com.example.habitflow.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HabitsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeHabitRepository
    private lateinit var viewModel: HabitsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeHabitRepository()
        viewModel = HabitsViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Carga de hábitos ---

    @Test
    fun cargarHabitosDeberiaCargarLosHabitosCorrectamente() = runTest {
        // dado
        val habitosIniciales = listOf(
            Habit(1L, "Leer 30 mins", "Libro de Kotlin", "Diario", false, "2026-10-06T10:00:00Z"),
        )
        fakeRepository.habits.addAll(habitosIniciales)

        // cuando
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Success)
        assertEquals(habitosIniciales, (state as HabitsUiState.Success).habits)
    }

    @Test
    fun cargarHabitosDeberiaManejarUnErrorAlCargarHabitos() = runTest {
        // dado
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Error de red"

        // cuando
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Error)
        assertEquals("Error de red", (state as HabitsUiState.Error).message)
    }

    // --- Validación ---

    @Test
    fun crearHabitoNoDeberiaPermitirCrearUnHabitoConTituloVacio() = runTest {
        // dado
        val tituloVacio = "   "
        val frecuencia = "Diario"

        // cuando
        viewModel.createHabit(tituloVacio, "Descripcion", frecuencia)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.createUiState.value
        assertTrue(state is CreateHabitUiState.ValidationError)
        assertEquals("El título es obligatorio", (state as CreateHabitUiState.ValidationError).titleError)
        assertEquals(0, fakeRepository.createHabitCallCount)
    }

    @Test
    fun crearHabitoNoDeberiaPermitirCrearUnHabitoConFrecuenciaVacia() = runTest {
        // dado
        val titulo = "Hacer ejercicio"
        val frecuenciaVacia = "   "

        // cuando
        viewModel.createHabit(titulo, "Gym por la mañana", frecuenciaVacia)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.createUiState.value
        assertTrue(state is CreateHabitUiState.ValidationError)
        assertEquals("La frecuencia es obligatoria", (state as CreateHabitUiState.ValidationError).frequencyError)
        assertEquals(0, fakeRepository.createHabitCallCount)
    }

    // --- Creación ---

    @Test
    fun crearHabitoDeberiaCrearCorrectamenteUnHabitoConDatosValidos() = runTest {
        // dado
        val titulo = "Meditar"
        val descripcion = "10 minutos guiados"
        val frecuencia = "Diario"

        // cuando
        viewModel.createHabit(titulo, descripcion, frecuencia)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.createUiState.value
        assertTrue(state is CreateHabitUiState.Success)
        assertEquals(1, fakeRepository.createHabitCallCount)
        assertEquals(1, fakeRepository.habits.size)
        assertEquals("Meditar", fakeRepository.habits.first().title)
    }

    @Test
    fun crearHabitoDeberiaManejarUnErrorAlCrearUnHabito() = runTest {
        // dado
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Error al insertar en Supabase"

        // cuando
        viewModel.createHabit("Estudiar", "Kotlin KMP", "Semanal")
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.createUiState.value
        assertTrue(state is CreateHabitUiState.Error)
        assertEquals("Error al insertar en Supabase", (state as CreateHabitUiState.Error).message)
    }

    // --- Actualización ---

    @Test
    fun actualizarHabitoDeberiaActualizarCorrectamenteUnHabito() = runTest {
        // dado
        val habitOriginal = Habit(1L, "Título viejo", "Desc vieja", "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habitOriginal)
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // cuando
        viewModel.updateHabit(1L, "Título nuevo", "Desc nueva", "Semanal")
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.createUiState.value
        assertTrue(state is CreateHabitUiState.Success)
        assertEquals(1, fakeRepository.updateHabitCallCount)
        val habitActualizado = fakeRepository.habits.first { it.id == 1L }
        assertEquals("Título nuevo", habitActualizado.title)
        assertEquals("Desc nueva", habitActualizado.description)
        assertEquals("Semanal", habitActualizado.frequency)
    }

    @Test
    fun actualizarHabitoDeberiaManejarUnErrorAlActualizarUnHabito() = runTest {
        // dado
        val habitOriginal = Habit(1L, "Título viejo", "Desc vieja", "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habitOriginal)
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Error al actualizar en Supabase"

        // cuando
        viewModel.updateHabit(1L, "Título nuevo", "Desc nueva", "Semanal")
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.createUiState.value
        assertTrue(state is CreateHabitUiState.Error)
        assertEquals("Error al actualizar en Supabase", (state as CreateHabitUiState.Error).message)
    }

    // --- Completar / Descompletar ---

    @Test
    fun completarHabitoDeberiaMarcarCorrectamenteUnHabitoComoCompletado() = runTest {
        // dado
        val habitPendiente = Habit(1L, "Correr", null, "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habitPendiente)
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // cuando
        viewModel.toggleHabitCompletion(habitPendiente)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Success)
        val habitActualizado = (state as HabitsUiState.Success).habits.first()
        assertTrue(habitActualizado.completed)
        assertEquals(1, fakeRepository.updateCompletionCallCount)
    }

    @Test
    fun completarHabitoDeberiaDesmarcarCorrectamenteUnHabitoCompletado() = runTest {
        // dado
        val habitCompletado = Habit(1L, "Correr", null, "Diario", true, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habitCompletado)
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // cuando
        viewModel.toggleHabitCompletion(habitCompletado)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Success)
        val habitActualizado = (state as HabitsUiState.Success).habits.first()
        assertFalse(habitActualizado.completed)
        assertEquals(1, fakeRepository.updateCompletionCallCount)
    }

    @Test
    fun completarHabitoNoDeberiaProvocarUnGetInnecesarioDeTodosLosHabitos() = runTest {
        // dado
        val habit = Habit(1L, "Caminar", null, "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habit)
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()
        val getHabitsCountInicial = fakeRepository.getHabitsCallCount

        // cuando
        viewModel.toggleHabitCompletion(habit)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        assertEquals(getHabitsCountInicial, fakeRepository.getHabitsCallCount)
    }

    @Test
    fun completarHabitoDeberiaActualizarUpdatingHabitIdsDuranteLaOperacion() = runTest {
        // dado
        val habit = Habit(1L, "Caminar", null, "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habit)
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // cuando & entonces (al finalizar)
        viewModel.toggleHabitCompletion(habit)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.updatingHabitIds.value.isEmpty())
    }

    // --- Eliminación ---

    @Test
    fun eliminarHabitoDeberiaEliminarCorrectamenteUnHabito() = runTest {
        // dado
        val habit1 = Habit(1L, "Habito 1", null, "Diario", false, "2026-10-06T10:00:00Z")
        val habit2 = Habit(2L, "Habito 2", null, "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.addAll(listOf(habit1, habit2))
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()

        // cuando
        viewModel.deleteHabit(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Success)
        assertEquals(1, (state as HabitsUiState.Success).habits.size)
        assertEquals(2L, state.habits.first().id)
        assertEquals(1, fakeRepository.deleteHabitCallCount)
    }

    @Test
    fun eliminarHabitoDeberiaManejarUnErrorAlEliminarUnHabito() = runTest {
        // dado
        val habit1 = Habit(1L, "Habito 1", null, "Diario", false, "2026-10-06T10:00:00Z")
        fakeRepository.habits.add(habit1)
        viewModel.loadHabits()
        testDispatcher.scheduler.advanceUntilIdle()
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Error al eliminar en Supabase"

        // cuando
        viewModel.deleteHabit(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // entonces
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Error)
        assertEquals("Error al eliminar en Supabase", (state as HabitsUiState.Error).message)
    }
}
