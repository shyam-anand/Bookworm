package com.shyamanand.bookworm.ui.screens.imagesearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shyamanand.bookworm.ui.state.ImageSearchScreenState

@Composable
fun ImageSearchScreen(
    state: ImageSearchScreenState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        when (state) {
            is ImageSearchScreenState.Init -> InitScreen(modifier)
            is ImageSearchScreenState.Loading -> TODO()
            is ImageSearchScreenState.Error -> TODO()
            is ImageSearchScreenState.TextDetected -> TODO()
        }

    }
}

@Composable
fun InitScreen(modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Hang on a sec")
    }
}
