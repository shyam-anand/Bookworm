package com.shyamanand.bookworm.ui.state

import androidx.compose.foundation.ScrollState
import com.shyamanand.bookworm.data.model.Book
import com.shyamanand.bookworm.network.model.ImageLinks
import com.shyamanand.bookworm.network.model.VolumeInfo

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