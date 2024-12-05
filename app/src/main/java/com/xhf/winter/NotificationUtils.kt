package com.xhf.winter
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationUtils {
    companion object{
        private const val CHANNEL_ID = "screen_capture_channel"

         fun createNotification(context: Context): Notification {
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Screen Capture Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_launcher_background) // 需要添加一个通知图标
                .build()
        }

         fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Screen Capture Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Screen capture service is running"
                }

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}