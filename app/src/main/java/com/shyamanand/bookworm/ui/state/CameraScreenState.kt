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

    data class Uploaded(
        val imageUri: Uri
    ) : CameraScreenState

    // Image captured
    data class PictureTaken(
        val imageUri: Uri?
    ) : CameraScreenState

    // Text detected in the capture image
    data class TextDetected(
        // The text that was detected
        val detectedText: List<String>,

        // The local image URI
        val imageUri: Uri
    ) : CameraScreenState

    data class Error(val e: Exception) : CameraScreenState
}
