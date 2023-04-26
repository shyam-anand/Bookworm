package com.shyamanand.bookworm.ui.state

import com.shyamanand.bookworm.network.model.SearchResult


sealed interface ResultsGridState {
    data class Success(val searchResult: SearchResult) : ResultsGridState
    data class Error(val error: String) : ResultsGridState

    object Loading: ResultsGridState
    object Empty : ResultsGridState
}