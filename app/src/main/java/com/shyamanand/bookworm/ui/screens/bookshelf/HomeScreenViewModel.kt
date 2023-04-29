package com.shyamanand.bookworm.ui.screens.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shyamanand.bookworm.BookwormApplication
import com.shyamanand.bookworm.data.BooksRepository
import com.shyamanand.bookworm.ui.state.HomeScreenState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(
    private val booksOfflineRepository: BooksRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookwormApplication)
                val booksOfflineRepository = application.container.booksOfflineRepository
                HomeScreenViewModel(booksOfflineRepository = booksOfflineRepository)
            }
        }
    }

    val homeScreenState: StateFlow<HomeScreenState> =
        booksOfflineRepository.getAllBooksStream()
            .map { HomeScreenState.Shelf(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeScreenState.Init
            )
}