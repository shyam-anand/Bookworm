package com.shyamanand.bookworm.ui.screens.camera

import android.Manifest
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.*
import com.shyamanand.bookworm.R
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.ui.theme.BookwormTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CameraScreenViewModel.Factory
    )
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context)
    }
    val cameraSelector = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    val imageCapture: MutableState<ImageCapture?> = remember {
        mutableStateOf(null)
    }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(previewView) {
        imageCapture.value = context.createImageCaptureUseCase(
            previewView, lifecycleOwner, cameraSelector.value
        )
    }


    if (cameraPermissionState.hasPermission) {
        Log.i(TAG, "Camera permission granted")

        CameraViewWinder(
            modifier = modifier,
            previewView = previewView,
            takePicture = { viewModel.takePicture(imageCapture.value!!, context) }
        )
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

@Preview
@Composable
fun CameraScreenPreview(modifier: Modifier = Modifier) {
    BookwormTheme {
        CameraScreen(modifier)
    }
}