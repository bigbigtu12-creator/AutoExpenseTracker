package com.example.autoexpense.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.autoexpense.R
import com.example.autoexpense.ui.ExpenseConfirmActivity
import com.example.autoexpense.util.NotificationParser

/**
 * 支付通知监听服务（前台服务）
 */
class PaymentNotificationService : NotificationListenerService() {

    private val tag = "PaymentNotificationSvc"
    private val FG_CHANNEL = "payment_listener_channel"
    private val ALERT_CHANNEL = "payment_alert_channel"
    private val FG_NOTIFICATION_ID = 1
    private val PAYMENT_NOTIFICATION_ID = 2001

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "=== 服务已创建 ===")
        createChannels()
        startForegroundNotification()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(tag, "=== 通知监听已连接 ===")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(tag, "=== 通知监听已断开 ===")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return
        if (sbn.packageName == packageName) return

        // 如果无障碍服务已启用，检查最近是否已处理过该通知（避免重复触发）
        // 注意：不能完全跳过，因为分组通知不触发无障碍事件
        if (PaymentAccessibilityService.instance != null) {
            Log.d(tag, "无障碍服务已启用，但 NLS 仍作为备用通道处理分组通知")
        }

        Log.d(tag, "收到通知（备用通道）: pkg=${sbn.packageName}")

        val result = NotificationParser.parse(sbn)
        if (result == null) {
            Log.d(tag, "非支付通知，跳过")
            return
        }

        Log.d(tag, "检测到支付通知")

        // 检测是否是收入/退款
        val isIncome = com.example.autoexpense.util.AmountExtractor.isIncomeNotification(result.rawText)
        val isRefund = com.example.autoexpense.util.AmountExtractor.isRefundNotification(result.rawText)
        val type = when {
            isRefund -> "refund"
            isIncome -> "income"
            else -> "expense"
        }

        // 如果是退款通知但金额为0，使用默认值
        val amount = if (isRefund && result.amount == 0.0) {
            Log.d(tag, "退款通知但金额为0，使用默认值")
            0.0
        } else {
            result.amount
        }

        // 两条监听通道统一交给 OverlayService 去重并展示，避免重复弹窗。
        Log.d(tag, "交给 OverlayService 统一展示...")
        try {
            val intent = Intent(this, OverlayService::class.java).apply {
                putExtra("amount", amount)
                putExtra("paymentMethod", result.paymentMethod.displayName)
                putExtra("rawText", result.rawText)
                putExtra("transactionType", type)
                putExtra("notificationKey", sbn.key)
            }
            startService(intent)
        } catch (e: Exception) {
            Log.e(tag, "启动 OverlayService 失败", e)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun createChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val fg = NotificationChannel(FG_CHANNEL, "支付通知监听", NotificationManager.IMPORTANCE_LOW)
        fg.description = "后台监听"

        val alert = NotificationChannel(ALERT_CHANNEL, "记账提醒", NotificationManager.IMPORTANCE_HIGH)
        alert.description = "支付提醒"
        alert.enableVibration(true)
        alert.enableLights(true)

        manager.createNotificationChannel(fg)
        manager.createNotificationChannel(alert)
        Log.d(tag, "通知渠道已创建")
    }

    private fun startForegroundNotification() {
        try {
            val notification = Notification.Builder(this, FG_CHANNEL)
                .setContentTitle("自动记账")
                .setContentText("正在监听支付通知...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build()
            startForeground(FG_NOTIFICATION_ID, notification)
            Log.d(tag, "✅ 前台服务已启动")
        } catch (e: Exception) {
            Log.e(tag, "❌ 启动前台服务失败", e)
        }
    }

    /**
     * 发送支付提醒通知
     */
    private fun showPaymentAlert(amount: Double, paymentMethod: String, rawText: String, transactionType: String = "expense") {
        Log.d(tag, "构建记账通知")

        val intent = Intent(this, ExpenseConfirmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("amount", amount)
            putExtra("paymentMethod", paymentMethod)
            putExtra("rawText", rawText)
            putExtra("transactionType", transactionType)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 根据交易类型显示不同标题
        val title = when (transactionType) {
            "income" -> "💰 收到 ¥${String.format("%.2f", amount)}"
            "refund" -> {
                if (amount == 0.0) "💸 退款通知" else "💸 退款 ¥${String.format("%.2f", amount)}"
            }
            else -> "💰 ¥${String.format("%.2f", amount)}"
        }

        val bigText = when {
            transactionType == "refund" && amount == 0.0 -> {
                "支付方式: $paymentMethod\n类型: 退款\n金额: 请在记账时手动输入\n\n点击打开记账对话框"
            }
            transactionType == "income" -> {
                "金额: ¥${String.format("%.2f", amount)}\n支付方式: $paymentMethod\n类型: 收入\n\n点击打开记账对话框"
            }
            transactionType == "refund" -> {
                "金额: ¥${String.format("%.2f", amount)}\n支付方式: $paymentMethod\n类型: 退款\n\n点击打开记账对话框"
            }
            else -> {
                "金额: ¥${String.format("%.2f", amount)}\n支付方式: $paymentMethod\n类型: 支出\n\n点击打开记账对话框"
            }
        }

        val notification = Notification.Builder(this, ALERT_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("点击记账 · $paymentMethod")
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
        manager.notify(PAYMENT_NOTIFICATION_ID, notification)
        Log.d(tag, "✅ 通知已发送 (ID=$PAYMENT_NOTIFICATION_ID)")
    }
}
