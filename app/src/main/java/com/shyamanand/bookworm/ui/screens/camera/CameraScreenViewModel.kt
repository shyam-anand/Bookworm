package com.shyamanand.bookworm.ui.screens.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shyamanand.bookworm.TAG
import java.text.SimpleDateFormat
import java.util.*

class CameraScreenViewModel : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CameraScreenViewModel()
            }
        }
        private const val TAG = "Bookworm"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

    fun takePicture(imageCapture: ImageCapture, context: Context) {
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bookworm")
            }
        }

        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, exception.stackTraceToString())
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.i(TAG, "Image saved: ${outputFileResults.savedUri}")
                }
            })
    }
}

fun Context.createImageCaptureUseCase(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector
): ImageCapture {
    val preview = androidx.camera.core.Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build()

    val cameraProvider = ProcessCameraProvider.getInstance(this).get()
    cameraProvider.unbindAll()
    val camera = cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageCapture
    )
    Log.i(
        TAG,
        "Use case binding succeeded. " +
                "cameraState = ${camera.cameraInfo.cameraState.value}"
    )

    return imageCapture

}