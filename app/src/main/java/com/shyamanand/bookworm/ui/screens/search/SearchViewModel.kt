package com.shyamanand.bookworm.ui.screens.search

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.shyamanand.bookworm.network.TextDetectionRepository
import com.shyamanand.bookworm.ui.state.ResultsGridState
import com.shyamanand.bookworm.ui.state.SearchState
import com.shyamanand.bookworm.ui.state.SearchbarState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.streams.toList

class SearchViewModel(
    private val booksOnlineRepository: BooksOnlineRepository,
    private val textDetectionRepository: TextDetectionRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookwormApplication)
                val booksRepository = application.container.booksOnlineRepository
                val textDetectionRepository = application.container.textDetectionRepository
                SearchViewModel(
                    booksOnlineRepository = booksRepository,
                    textDetectionRepository = textDetectionRepository
                )
            }
        }
    }

    var searchbarState: SearchbarState by mutableStateOf(SearchbarState.Empty)
        private set

    var resultsGridState: ResultsGridState by mutableStateOf(ResultsGridState.Empty)
        private set

    private var searchState: SearchState by mutableStateOf(SearchState())

    private fun search(title: String) {
        val searchJob = viewModelScope.launch {
            resultsGridState = ResultsGridState.Loading

            resultsGridState = try {
                val searchResult = booksOnlineRepository.search(title)
                Log.i(
                    TAG,
                    "Found ${searchResult.totalItems} results for title $title"
                )
                if (searchResult.totalItems > 0) {
                    val books = searchResult.items.stream()
                        .map { it.toBook() }
                        .toList()
                    ResultsGridState.Success(books)
                } else {
                    ResultsGridState.Loading
                }
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
                ResultsGridState.Error(e.stackTraceToString())
            } catch (e: HttpException) {
                Log.e(TAG, e.stackTraceToString())
                ResultsGridState.Error(e.stackTraceToString())
            }
        }
        searchState = SearchState(searchJob)
    }

    fun onSearchbarInput(searchString: String) {
        Log.i(TAG, "searchString=$searchString")
        searchbarState = SearchbarState.HasInput(searchString)
        searchState.searchJob?.cancel()
        if (searchString.length > 3) {
            resultsGridState = ResultsGridState.Loading
            search(searchString)
        } else {
            resultsGridState = ResultsGridState.Empty
        }
    }

    fun search() {
        if (searchbarState is SearchbarState.HasInput) {
            search((searchbarState as SearchbarState.HasInput).searchString)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun imageSearch(imageUri: Uri, context: Context) {
        searchbarState = SearchbarState.ImageSearch(imageUri)
        resultsGridState = ResultsGridState.Loading
        val imageSearchJob = viewModelScope.launch {
            try {
                val uploadedImage = textDetectionRepository.upload(imageUri, context)
                val detectedText = textDetectionRepository.detectText(uploadedImage.name)
                Log.i(TAG, "Received detected text: $detectedText")

                if (detectedText.isNotEmpty()) {
                    search(detectedText.joinToString(" "))
                } else {
                    resultsGridState = ResultsGridState.Error("No matches found")
                }

            } catch (e: IOException) {
                Log.e(TAG, "IOException: while detecting text: " + e.stackTraceToString())
                ResultsGridState.Error(e.stackTraceToString())
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException while detecting text: " + e.stackTraceToString())
                ResultsGridState.Error(e.stackTraceToString())
            } catch (e: Exception) {
                Log.e(TAG, "${e.javaClass} while detecting text: ${e.stackTraceToString()}")
                ResultsGridState.Error(e.stackTraceToString())
            }
        }

        searchState = SearchState(imageSearchJob)
    }

    fun resetSearchbar() {
        searchState.searchJob?.cancel()
        searchbarState = SearchbarState.Empty
        resultsGridState = ResultsGridState.Empty
    }
}