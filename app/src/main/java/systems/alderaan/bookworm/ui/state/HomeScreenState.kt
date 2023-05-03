package systems.alderaan.bookworm.ui.state

import systems.alderaan.bookworm.data.model.Book

sealed interface HomeScreenState {

    data class Shelf(val books: List<Book> = listOf()) : HomeScreenState

    object Search : HomeScreenState
    object Init : HomeScreenState
}
