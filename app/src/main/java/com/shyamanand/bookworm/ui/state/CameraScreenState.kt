package com.shyamanand.bookworm.ui.state

import android.net.Uri
import java.lang.Exception

sealed interface CameraScreenState {
    // Initial state
    object Init : CameraScreenState

    // Camera is ready to take picture. Shows view finder.
    object Preview : CameraScreenState

    // Uploading, and detecting text
    data class Loading(
        val imageUri: Uri
    ) : CameraScreenState

    // Image captured
    data class PictureTaken(
        val imageUri: Uri?
    ) : CameraScreenState

    data class Error(val e: Exception) : CameraScreenState
}
