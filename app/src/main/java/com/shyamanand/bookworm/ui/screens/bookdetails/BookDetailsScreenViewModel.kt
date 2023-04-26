package com.shyamanand.bookworm.ui.screens.bookdetails

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
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.data.BooksOnlineRepository
import com.shyamanand.bookworm.data.BooksRepository
import com.shyamanand.bookworm.data.model.Book
import com.shyamanand.bookworm.ui.state.BookDetailsScreenState
import com.shyamanand.bookworm.ui.state.isValid
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookDetailsScreenViewModel(
    private val booksOnlineRepository: BooksOnlineRepository,
    private val booksOfflineRepository: BooksRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookwormApplication)
                val booksOnlineRepository = application.container.booksOnlineRepository
                val booksOfflineRepository = application.container.booksOfflineRepository
                BookDetailsScreenViewModel(
                    booksOnlineRepository = booksOnlineRepository,
                    booksOfflineRepository = booksOfflineRepository
                )
            }
        }
    }

    var state: BookDetailsScreenState by mutableStateOf(BookDetailsScreenState.Loading)

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            state = BookDetailsScreenState.Loading

            Log.d(TAG, "loading $bookId")
            state = try {
                fetchBook(bookId)
            } catch (e: IOException) {
                Log.e(TAG, "IOException: " + e.stackTraceToString())
                BookDetailsScreenState.Error
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException: " + e.stackTraceToString())
                BookDetailsScreenState.Error
            }
        }
    }

    private suspend fun fetchBook(bookId: String): BookDetailsScreenState {
        var book = booksOfflineRepository.getBook(bookId)
        var inShelf = true
        if (book == null) {
            inShelf = false
            book = booksOnlineRepository.getDetails(bookId).toBook()
        }

        return BookDetailsScreenState.Success(
            bookId = bookId,
            book = book,
            inShelf = inShelf
        )
    }

    private fun getBookFromState(): Book {
        if (state !is BookDetailsScreenState.Success) {
            throw IllegalStateException("Invalid state")
        }
        val successState = (state as BookDetailsScreenState.Success)
        if (successState.isValid()) {
            return successState.book
        }
        throw IllegalStateException("Invalid state")
    }

    suspend fun addBook() {
        val book = getBookFromState()
        booksOfflineRepository.addBook(book)
        state = BookDetailsScreenState.Success(book.id, book, true)

    }

    suspend fun removeBook() {
        val book = getBookFromState()
        booksOfflineRepository.deleteBook(book)
        state = BookDetailsScreenState.Success(book.id, book, false)
    }
}