package systems.alderaan.bookworm.ui.screens.home

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
import systems.alderaan.bookworm.BookwormApplication
import systems.alderaan.bookworm.data.BooksRepository
import systems.alderaan.bookworm.ui.state.HomeScreenState
import kotlinx.coroutines.launch
import systems.alderaan.bookworm.data.model.Book

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

    private var shelf: List<Book> = emptyList()

    private suspend fun reloadShelf() {
        booksOfflineRepository.getAllBooksStream()
            .collect {
                shelf = it
                state = HomeScreenState.Shelf(shelf)
            }
    }

    init {
        viewModelScope.launch {
            reloadShelf()
        }
    }

    fun onSearchbarInput(searchString: String) {
        state = if (searchString.isNotEmpty()) {
            HomeScreenState.Search
        } else {
            Log.i(tag, "Setting state to Shelf")
            HomeScreenState.Shelf(shelf)
        }
    }

    fun resetState() {
        Log.i(tag, "Resetting state")
        state = HomeScreenState.Shelf(shelf)
    }
}