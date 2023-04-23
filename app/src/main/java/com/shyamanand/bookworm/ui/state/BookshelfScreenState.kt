package com.shyamanand.bookworm.ui.state

import com.shyamanand.bookworm.data.model.Book

data class BookshelfScreenState(
    val books: List<Book> = listOf()
)
