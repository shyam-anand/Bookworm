package systems.alderaan.bookworm.ui.state

import systems.alderaan.bookworm.data.model.Book

sealed interface BookDetailsScreenState{
    data class Success(
        val bookId: String,
        val book: Book,
        val inShelf: Boolean
    ) : BookDetailsScreenState
    object Error: BookDetailsScreenState
    object Loading: BookDetailsScreenState
}

fun BookDetailsScreenState.Success.isValid() : Boolean {
    return bookId.isNotEmpty() && book.title.isNotEmpty()
}