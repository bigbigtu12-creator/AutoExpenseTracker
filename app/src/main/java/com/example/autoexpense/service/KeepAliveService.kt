package com.example.autoexpense.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.autoexpense.R

/**
 * 保活服务
 * 以前台服务方式运行，防止系统杀掉 App 和通知监听服务
 */
class KeepAliveService : Service() {

    private val tag = "KeepAliveService"

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "保活服务已创建")
        startForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "保活服务已启动")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundNotification() {
        try {
            val channelId = "keep_alive_channel"
            val channel = NotificationChannel(
                channelId,
                "后台服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "保持记账服务运行"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val notification = Notification.Builder(this, channelId)
                .setContentTitle("自动记账运行中")
                .setContentText("正在监听支付通知...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build()

            startForeground(2, notification)
            Log.d(tag, "前台通知已启动")
        } catch (e: Exception) {
            Log.e(tag, "启动前台通知失败", e)
        }
    }
}
