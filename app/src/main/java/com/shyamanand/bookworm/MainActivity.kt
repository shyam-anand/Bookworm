package com.shyamanand.bookworm

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.shyamanand.bookworm.ui.BookwormApp
import com.shyamanand.bookworm.ui.theme.BookwormTheme

const val TAG = "BookwormApp"

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookwormTheme {
                BookwormApp()
            }
        }

    }
}