package com.shyamanand.bookworm.ui

import androidx.lifecycle.ViewModel
import com.shyamanand.bookworm.ui.state.AppUiState
import com.shyamanand.bookworm.ui.common.BookwormAppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BookwormAppViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState.SearchScreen)
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun setBookDetailsScreen(bookId: String) {
        _uiState.update { currentState ->
            currentState.copy(screen = BookwormAppScreen.BookDetails, bookId = bookId)
        }
    }
}
