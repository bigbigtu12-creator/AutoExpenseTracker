package com.example.autoexpense.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 开机自启接收器
 * 设备重启后，尝试重新启动通知监听服务
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "开机完成，尝试重启通知服务")
            // 通知监听服务由系统自动管理，这里只是记录日志
            // 用户需要在设置中重新开启通知使用权
        }
    }
}
