package com.shyamanand.bookworm.ui.state

data class SearchbarState(
    val searchString: String
) {
    companion object {
        val Empty: SearchbarState = SearchbarState(
            searchString = ""
        )
    }
}