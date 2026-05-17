package com.discordbotmaker.android.feature.console

import com.discordbotmaker.android.feature.createbot.FakeBotMakerRepository
import com.discordbotmaker.android.data.model.BotLogEntryDto
import com.discordbotmaker.android.data.model.LogLevel
import com.discordbotmaker.android.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConsoleViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun `connect marks stream connected and appends logs`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = ConsoleViewModel(repository)

        viewModel.connect()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isConnected)
        assertTrue(viewModel.uiState.value.logs.isNotEmpty())
    }

    @Test
    fun `disconnect closes stream`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = ConsoleViewModel(repository)

        viewModel.connect()
        advanceUntilIdle()
        viewModel.disconnect()

        assertFalse(viewModel.uiState.value.isConnected)
    }

    @Test
    fun `clear logs empties source state until new logs arrive`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = ConsoleViewModel(repository)

        viewModel.connect()
        advanceUntilIdle()
        viewModel.clearLogs()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.logs.isEmpty())

        repository.emitLog()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.logs.size)
    }

    @Test
    fun `connect can recover after stream error`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = ConsoleViewModel(repository)

        viewModel.connect()
        advanceUntilIdle()
        repository.failLogStream("Socket crashed")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isConnected)
        assertEquals("Socket crashed", viewModel.uiState.value.errorMessage)

        viewModel.connect()
        advanceUntilIdle()

        assertEquals(2, repository.openLogStreamCalls)
        assertTrue(viewModel.uiState.value.isConnected)
        assertEquals(null, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `connect can recover after stream disconnect`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = ConsoleViewModel(repository)

        viewModel.connect()
        advanceUntilIdle()
        repository.disconnectLogStream()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isConnected)

        viewModel.connect()
        advanceUntilIdle()

        assertEquals(2, repository.openLogStreamCalls)
        assertTrue(viewModel.uiState.value.isConnected)
    }

    @Test
    fun `reconnect clears previous error once stream opens again`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = ConsoleViewModel(repository)

        viewModel.connect()
        advanceUntilIdle()
        repository.failLogStream("Temporary network error")
        advanceUntilIdle()

        assertEquals("Temporary network error", viewModel.uiState.value.errorMessage)

        viewModel.connect()
        advanceUntilIdle()

        assertEquals(null, viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.isConnected)
    }
}
