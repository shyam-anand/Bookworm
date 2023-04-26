package com.shyamanand.bookworm.ui.screens.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.google.accompanist.permissions.*
import com.shyamanand.bookworm.R
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.ui.state.CameraScreenState

@Composable
fun CameraScreen(
    state: CameraScreenState,
    onPermissionGranted: () -> Unit,
    onTakePicture: (ImageCapture, Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture: MutableState<ImageCapture?> = remember { mutableStateOf(null) }
    val cameraSelector = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }


    LaunchedEffect(previewView) {
        imageCapture.value = context.createImageCaptureUseCase(
                previewView, lifecycleOwner, cameraSelector.value
            )
    }

    when (state) {
        CameraScreenState.Init -> CameraPermissionScreen(
            onCameraPermissionGranted = onPermissionGranted
        )
        CameraScreenState.Preview -> CameraViewWinder(
            modifier = modifier,
            previewView = previewView,
            takePicture = { onTakePicture(imageCapture.value!!, context) }
        )
        is CameraScreenState.PictureTaken -> CapturedImage(
            imageUri = state.imageUri,
            modifier = modifier,
        )
        is CameraScreenState.TextDetected -> ImageWithText(
            imageUri = state.imageUri,
            text = state.detectedText,
            modifier = modifier,
        )
        is CameraScreenState.Error -> ErrorScreen(state.e)
        is CameraScreenState.Uploaded -> ImageUploaded(imageUri = state.imageUri)
        is CameraScreenState.Loading -> ImageWithText(imageUri = state.imageUri, text = listOf("Hang on a sec"))
    }
}

@Composable
fun ErrorScreen(e: Exception, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val message = e.localizedMessage
        message?.let {
            Text(text = message, style = MaterialTheme.typography.displayLarge)
        }
        Text(text = e.stackTraceToString(), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ImageWithText(
    imageUri: Uri?,
    text: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Log.i(TAG, "Displaying captured image: $imageUri")
        AsyncImage(model = imageUri, contentDescription = null)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            text.forEach { t ->
                Text(t)
            }
        }
    }
}

@Composable
fun CapturedImage(
    imageUri: Uri?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        imageUri?.let {
            Log.i(TAG, "Displaying captured image: $it")
            AsyncImage(model = it, contentDescription = null)
        }
    }
}

@Composable
fun ImageUploaded(
    imageUri: Uri?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        imageUri?.let {
            AsyncImage(model = imageUri, contentDescription = null)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Image uploaded.")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionScreen(
    onCameraPermissionGranted: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.hasPermission) {
        onCameraPermissionGranted()
    } else {
        Log.w(TAG, "Does not have camera permission.")
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera permission required")
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Grant permissions")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraViewWinder(
    modifier: Modifier = Modifier,
    previewView: PreviewView,
    takePicture: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Log.i(TAG, "Starting camera.")
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                onClick = takePicture,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .size(64.dp)
                    .background(Color.Transparent),
                shape = CircleShape,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = "Click!",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}