package com.shyamanand.bookworm.ui.screens.imagesearch

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shyamanand.bookworm.BookwormApplication
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.network.TextDetectionRepository
import com.shyamanand.bookworm.ui.state.ImageSearchScreenState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class ImageSearchScreenViewModel(
    private val textDetectionRepository: TextDetectionRepository
) : ViewModel() {

    var state: ImageSearchScreenState by mutableStateOf(ImageSearchScreenState.Init)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookwormApplication)
                val textDetectionRepository = application.container.textDetectionRepository
                ImageSearchScreenViewModel(
                    textDetectionRepository = textDetectionRepository
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun detectText(imageUri: Uri, context: Context) {
        state = ImageSearchScreenState.Loading(imageUri)
        viewModelScope.launch {
            state = try {
                val uploadedImage = textDetectionRepository.upload(imageUri, context)
                val detectedText = textDetectionRepository.detectText(uploadedImage.name)
                Log.i(TAG, "Received detected text: $detectedText")
                ImageSearchScreenState.TextDetected(imageUri, detectedText)
            } catch (e: IOException) {
                Log.e(TAG, "IOException: while detecting text: " + e.stackTraceToString())
                ImageSearchScreenState.Error(e.stackTraceToString())
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException while detecting text: " + e.stackTraceToString())
                ImageSearchScreenState.Error(e.stackTraceToString())
            }
        }
    }
}