package com.shyamanand.bookworm.ui.state

import android.net.Uri

sealed interface SearchbarState {
    object Empty : SearchbarState
    data class HasInput(val searchString: String) : SearchbarState
    data class ImageSearch(val image: Uri) : SearchbarState

}