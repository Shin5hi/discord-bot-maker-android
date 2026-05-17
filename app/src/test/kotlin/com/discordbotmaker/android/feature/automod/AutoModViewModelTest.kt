package com.discordbotmaker.android.feature.automod

import com.discordbotmaker.android.feature.createbot.FakeBotMakerRepository
import com.discordbotmaker.android.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutoModViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun `load gets current automod config`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = AutoModViewModel(repository)

        advanceUntilIdle()

        assertEquals(0.7f, viewModel.uiState.value.config.sensitivity)
    }

    @Test
    fun `save persists updated config`() = runTest {
        val repository = FakeBotMakerRepository()
        val viewModel = AutoModViewModel(repository)
        advanceUntilIdle()

        viewModel.save(viewModel.uiState.value.config.copy(enabled = true, spamThreshold = 9))
        advanceUntilIdle()

        assertTrue(repository.savedAutoMod.enabled)
        assertEquals(9, repository.savedAutoMod.spamThreshold)
    }
}
