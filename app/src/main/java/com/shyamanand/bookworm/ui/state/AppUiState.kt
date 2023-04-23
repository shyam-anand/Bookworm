package com.shyamanand.bookworm.ui.state

import com.shyamanand.bookworm.ui.common.BookwormAppScreen

data class AppUiState(
    val screen: BookwormAppScreen = BookwormAppScreen.Search,
    val bookId: String? = null
) {
    companion object {
        val SearchScreen: AppUiState = AppUiState(BookwormAppScreen.Search)
    }
}
