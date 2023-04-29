package com.shyamanand.bookworm.ui.state

import com.shyamanand.bookworm.data.model.Book

sealed interface HomeScreenState {

    data class Shelf(val books: List<Book> = listOf()) : HomeScreenState

    object Search : HomeScreenState
    object Init : HomeScreenState
}
