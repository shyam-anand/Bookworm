package com.shyamanand.bookworm.ui.screens

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
import com.shyamanand.bookworm.ui.state.ResultsGridState
import com.shyamanand.bookworm.ui.state.SearchbarState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SearchScreenViewModel(
    private val booksOnlineRepository: BooksOnlineRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookwormApplication)
                val booksRepository = application.container.booksOnlineRepository
                SearchScreenViewModel(booksOnlineRepository = booksRepository)
            }
        }
    }

    var searchbarState: SearchbarState by mutableStateOf(SearchbarState.Empty)
        private set

    var resultsGridState: ResultsGridState by mutableStateOf(ResultsGridState.Loading)
        private set

    private fun search(title: String) {
        viewModelScope.launch {
            resultsGridState = ResultsGridState.Loading

            resultsGridState = try {
                val searchResult = booksOnlineRepository.search(title)
                Log.i(
                    TAG,
                    "Found ${searchResult.totalItems} results for title $title"
                )
                if (searchResult.totalItems > 0) {
                    ResultsGridState.Success(searchResult)
                } else {
                    ResultsGridState.Loading
                }
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
                ResultsGridState.Error
            } catch (e: HttpException) {
                Log.e(TAG, e.stackTraceToString())
                ResultsGridState.Error
            }
        }
    }

    fun onSearchbarInput(searchString: String) {
        searchbarState = SearchbarState(searchString)
        if (searchString.length > 3) {
            Log.i(TAG, "searchString=$searchString")
            search(searchString)
        } else {
            resultsGridState = ResultsGridState.Loading
        }
    }

    fun search() {
        search(searchbarState.searchString)
    }
}