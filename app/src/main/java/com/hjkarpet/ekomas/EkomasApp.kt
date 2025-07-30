// EkomasApp.kt
package com.hjkarpet.ekomas

import android.app.Application
import com.cloudinary.android.MediaManager

class EkomasApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )
        MediaManager.init(this, config)
    }
}