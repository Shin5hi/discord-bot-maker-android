package com.discordbotmaker.android.feature.createbot

import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.AutoModConfigDto
import com.discordbotmaker.android.data.model.BotConfigDto
import com.discordbotmaker.android.data.model.BotLogEntryDto
import com.discordbotmaker.android.data.model.BotStatus
import com.discordbotmaker.android.data.model.LogLevel
import com.discordbotmaker.android.data.model.ToxicityAction
import com.discordbotmaker.android.testutil.MainDispatcherRule
import java.io.Closeable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBotViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun `submit rejects blank backend url`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = CreateBotViewModel(repository)

        viewModel.submit(botName = "Grid", token = VALID_TOKEN)
        advanceUntilIdle()

        assertEquals("Enter a backend URL first.", viewModel.uiState.value.errorMessage)
        assertFalse(repository.registerCalled)
    }

    @Test
    fun `submit saves backend url and registers bot`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = CreateBotViewModel(repository)

        viewModel.updateBackendUrl("http://192.168.1.20:8000/")
        viewModel.submit(botName = "Grid", token = VALID_TOKEN)
        advanceUntilIdle()

        assertTrue(repository.registerCalled)
        assertEquals("http://192.168.1.20:8000", repository.savedBackendUrl)
        assertEquals("Grid", viewModel.uiState.value.lastRegisteredBot?.botName)
    }
}
