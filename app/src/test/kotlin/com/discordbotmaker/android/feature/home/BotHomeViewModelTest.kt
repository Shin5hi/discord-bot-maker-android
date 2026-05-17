package com.discordbotmaker.android.feature.home

import com.discordbotmaker.android.feature.createbot.FakeBotMakerRepository
import com.discordbotmaker.android.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BotHomeViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun `start bot updates status to running`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = BotHomeViewModel(repository)

        viewModel.refresh()
        advanceUntilIdle()
        viewModel.startBot()
        advanceUntilIdle()

        assertEquals("RUNNING", viewModel.uiState.value.bot?.status?.name)
    }

    @Test
    fun `stop bot updates status to stopped`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = BotHomeViewModel(repository)

        viewModel.startBot()
        advanceUntilIdle()
        viewModel.stopBot()
        advanceUntilIdle()

        assertEquals("STOPPED", viewModel.uiState.value.bot?.status?.name)
    }
}
