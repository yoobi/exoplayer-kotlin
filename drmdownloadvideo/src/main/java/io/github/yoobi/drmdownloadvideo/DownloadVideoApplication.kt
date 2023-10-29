package io.github.yoobi.drmdownloadvideo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class DownloadVideoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}
