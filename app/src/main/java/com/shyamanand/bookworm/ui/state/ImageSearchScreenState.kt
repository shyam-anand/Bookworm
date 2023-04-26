package com.shyamanand.bookworm.ui.state

import android.net.Uri

sealed interface ImageSearchScreenState {

    object Init : ImageSearchScreenState
    data class Loading(val imageUri: Uri) : ImageSearchScreenState
    data class TextDetected(val imageUri: Uri, val text: List<String>) : ImageSearchScreenState
    data class Error(val message: String) : ImageSearchScreenState
}
