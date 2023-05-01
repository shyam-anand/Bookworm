package com.shyamanand.bookworm.ui.screens.bookshelf

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shyamanand.bookworm.BookwormApplication
import com.shyamanand.bookworm.data.BooksRepository
import com.shyamanand.bookworm.ui.state.HomeScreenState
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val booksOfflineRepository: BooksRepository
) : ViewModel() {
    private val tag = "HomeScreenViewModel"

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookwormApplication)
                val booksOfflineRepository = application.container.booksOfflineRepository
                HomeScreenViewModel(booksOfflineRepository = booksOfflineRepository)
            }
        }
    }

    var state: HomeScreenState by mutableStateOf(HomeScreenState.Init)
        private set

    init {
        viewModelScope.launch {
            setHomeScreenShelf()
        }
    }

    private suspend fun setHomeScreenShelf() {
        booksOfflineRepository.getAllBooksStream()
            .collect {
                state = HomeScreenState.Shelf(it)
            }
    }

    fun onSearchbarInput(searchString: String) {
        if (searchString.isNotEmpty()) {
            state = HomeScreenState.Search
        } else {
            Log.i(tag, "Setting state to Shelf")
            viewModelScope.launch {
                setHomeScreenShelf()
            }
        }
    }

    fun resetState() {
        viewModelScope.launch {
            setHomeScreenShelf()
        }
    }
}