package com.rovenhook.rsshool2021_android_task_musicapp.sevices

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.upstream.cache.*
import com.rovenhook.rsshool2021_android_task_musicapp.MainActivity
import com.rovenhook.rsshool2021_android_task_musicapp.R
import com.rovenhook.rsshool2021_android_task_musicapp.utils.MyApplication

class PlayerService() : Service() {
    private val binder = MyBinder()

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    inner class MyBinder() : Binder() {
        fun getPlayerService() = this@PlayerService
    }

    fun showToast() {
        Toast.makeText(this, "service toast!", Toast.LENGTH_LONG).show()
    }

    fun showLog() {
        Log.e("log-tag", "service log!")
    }

    fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
            .apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                this.action = Intent.ACTION_MAIN
//                this.addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val builder = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.spin_wheel)
            .setContentTitle("My notification")
            .setContentText("This is my notification i am proud of")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }
}
