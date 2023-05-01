package systems.alderaan.bookworm.ui.state

import systems.alderaan.bookworm.data.model.Book


sealed interface ResultsGridState {
    data class Success(val searchResult: List<Book>) : ResultsGridState
    data class Error(val error: String) : ResultsGridState

    object Loading: ResultsGridState
    object Empty : ResultsGridState
}