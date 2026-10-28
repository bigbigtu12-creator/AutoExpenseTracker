package com.example.autoexpense.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.example.autoexpense.R
import com.example.autoexpense.ui.ExpenseConfirmActivity

/**
 * 浮窗服务 - 发 Heads-up 通知
 */
class OverlayService :
    Service() {

    private val tag = "OverlayService"
    private val CHANNEL_ID = "expense_overlay_channel"
    private val handler = Handler(Looper.getMainLooper())
    private val alipayBurst = AlipayBurstAccumulator()

    companion object {
        // NLS 与无障碍服务可能先后收到同一条交易通知。
        private var lastFingerprint = ""
        private var lastTriggerTime = 0L
        private const val DEDUP_INTERVAL_MS = 8000L
        private const val ALIPAY_BURST_MS = 2000L
        private var nextNotificationId = 1001
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "OverlayService 已创建")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val amount = intent?.getDoubleExtra("amount", 0.0) ?: 0.0
        val paymentMethod = intent?.getStringExtra("paymentMethod") ?: "未知"
        val rawText = intent?.getStringExtra("rawText") ?: ""
        val transactionType = intent?.getStringExtra("transactionType") ?: "expense"
        val notificationKey = intent?.getStringExtra("notificationKey")

        if (transactionType == "expense" && paymentMethod == "支付宝" && amount > 0.0) {
            val first = alipayBurst.add(amount, rawText, notificationKey, SystemClock.elapsedRealtime())
            if (first) {
                handler.postDelayed({
                    alipayBurst.drain()?.let { batch ->
                        Log.d(tag, "支付宝连续通知: ${batch.count} 笔")
                        showExpenseNotification(batch.amount, paymentMethod, batch.rawText, transactionType, batch.count)
                    }
                }, ALIPAY_BURST_MS)
            }
            return START_NOT_STICKY
        }

        // 去重：防止 NLS 和 A11y 双通道先后触发同一笔交易。
        val now = System.currentTimeMillis()
        val fingerprint = "$transactionType|${String.format("%.2f", amount)}|$paymentMethod"
        if (fingerprint == lastFingerprint && (now - lastTriggerTime) < DEDUP_INTERVAL_MS) {
            Log.d(tag, "重复触发，跳过")
            return START_NOT_STICKY
        }
        lastFingerprint = fingerprint
        lastTriggerTime = now

        Log.d(tag, "收到记账请求")
        showExpenseNotification(amount, paymentMethod, rawText, transactionType)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "记账通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "支付后弹出记账确认"
            enableVibration(true)
            enableLights(true)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        Log.d(tag, "通知渠道已创建")
    }

    private fun showExpenseNotification(amount: Double, paymentMethod: String, rawText: String, transactionType: String = "expense", itemCount: Int = 1) {
        Log.d(tag, "发送记账通知")
        val notificationId = nextNotificationId++

        val openIntent = Intent(this, ExpenseConfirmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("amount", amount)
            putExtra("paymentMethod", paymentMethod)
            putExtra("rawText", rawText)
            putExtra("transactionType", transactionType)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 根据交易类型显示不同标题
        val (title, bigText) = when (transactionType) {
            "income" -> Pair(
                "💰 收到 ¥${String.format("%.2f", amount)}",
                "金额: ¥${String.format("%.2f", amount)}\n支付方式: $paymentMethod\n类型: 收入\n\n点击此通知打开记账对话框"
            )
            "refund" -> {
                if (amount == 0.0) {
                    Pair(
                        "💸 退款通知",
                        "支付方式: $paymentMethod\n类型: 退款\n金额: 请在记账时手动输入\n\n点击此通知打开记账对话框"
                    )
                } else {
                    Pair(
                        "💸 退款 ¥${String.format("%.2f", amount)}",
                        "金额: ¥${String.format("%.2f", amount)}\n支付方式: $paymentMethod\n类型: 退款\n\n点击此通知打开记账对话框"
                    )
                }
            }
            else -> Pair(
                if (itemCount > 1) "💰 检测到 $itemCount 笔支付合计 ¥${String.format("%.2f", amount)}"
                else "💰 检测到支付 ¥${String.format("%.2f", amount)}",
                "金额: ¥${String.format("%.2f", amount)}\n支付方式: $paymentMethod\n类型: 支出\n\n点击此通知确认记账"
            )
        }

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("$paymentMethod · 点击记账")
            .setStyle(
                Notification.BigTextStyle().bigText(bigText)
            )
            .setPriority(Notification.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
        Log.d(tag, "✅ 通知已发送 (ID=$notificationId)")
    }
}
