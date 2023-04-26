package com.shyamanand.bookworm.ui.screens.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.ui.state.CameraScreenState
import java.text.SimpleDateFormat
import java.util.*

class CameraScreenViewModel : ViewModel() {

    var state: CameraScreenState by mutableStateOf(CameraScreenState.Init)

    companion object {

        private const val TAG = "Bookworm"
        private const val FILENAME_FORMAT = "yyyyMMddHHmmss"

        private val name = "BW" + SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH)
            .format(System.currentTimeMillis())

        private val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bookworm")
            }
        }

        private fun buildOutputFileOptions(context: Context): ImageCapture.OutputFileOptions {
            return ImageCapture.OutputFileOptions
                .Builder(
                    context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()
        }

    }

    fun takePicture(imageCapture: ImageCapture, context: Context) {

        val outputFileOptions = buildOutputFileOptions(context)

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        context,
                        // ToDo: Handle this exception
                        "Failed to capture image: ${exception.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e(TAG, exception.stackTraceToString())
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri?.let {
                        Log.i(TAG, "Image saved: $it")
                        state = CameraScreenState.PictureTaken(it)
                    }
                }
            })
    }

    fun permissionGranted() {
        Log.i(TAG, "Camera permission granted. Opening camera.")
        state = CameraScreenState.Preview
    }

    fun showCameraPreview() {
        state = CameraScreenState.Preview
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