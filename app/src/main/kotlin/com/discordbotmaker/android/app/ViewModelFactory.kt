package com.discordbotmaker.android.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.discordbotmaker.android.data.BotMakerRepository

class RepositoryViewModelFactory<T : ViewModel>(
    private val create: () -> T,
) : ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
}

fun <T : ViewModel> viewModelFactory(create: () -> T): ViewModelProvider.Factory =
    RepositoryViewModelFactory(create)
