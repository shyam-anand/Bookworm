package com.shyamanand.bookworm.ui.state

import com.shyamanand.bookworm.network.model.SearchResult


sealed interface ResultsGridState {
    data class Success(val searchResult: SearchResult) : ResultsGridState

    object Error : ResultsGridState

    object Loading: ResultsGridState
}