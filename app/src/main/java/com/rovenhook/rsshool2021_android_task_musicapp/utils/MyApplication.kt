package com.rovenhook.rsshool2021_android_task_musicapp.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.rovenhook.rsshool2021_android_task_musicapp.R

class MyApplication : Application() {

    companion object {
        public final val CHANNEL_ID = "3252345"

        private var instance: MyApplication? = null

        fun getInstance(): MyApplication {
            if (instance == null) {
                MyApplication()
            }
            return requireNotNull(instance)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Test channel name"
            val descriptionText = "Test channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
