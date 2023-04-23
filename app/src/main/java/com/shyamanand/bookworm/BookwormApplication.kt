package com.shyamanand.bookworm

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.shyamanand.bookworm.container.AppContainer
import com.shyamanand.bookworm.container.DefaultAppContainer

class BookwormApplication : Application(), CameraXConfig.Provider {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }

}