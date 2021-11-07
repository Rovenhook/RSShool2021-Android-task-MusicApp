package com.rovenhook.rsshool2021_android_task_musicapp.utils

import android.app.Application

class MyApplication : Application() {

    companion object {
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
    }
}
