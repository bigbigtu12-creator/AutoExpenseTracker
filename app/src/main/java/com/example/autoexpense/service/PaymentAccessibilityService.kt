package com.example.autoexpense.service

import android.app.Notification
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo

/**
 * 支付通知无障碍监听服务
 *
 * 使用 AccessibilityService 替代 NotificationListenerService，
 * 解决 OnePlus Android 16 上 NLS 在后台被系统 unbind 杀掉的问题。
 *
 * AccessibilityService 的生命周期由系统管理，即使 App 在后台也不会被 unbind，
 * 适合需要长时间稳定运行的后台监听场景。
 */
class PaymentAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "PaymentA11yService"

        /**
         * 静态实例引用，供外部检查服务是否正在运行
         */
        @Volatile
        var instance: PaymentAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "=== 无障碍服务已连接 ===")

        // 动态配置服务信息（也可以通过 XML 配置）
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // 只处理通知栏事件
        if (event.eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) return

        // 获取通知的包名
        val packageName = event.packageName?.toString() ?: return

        // 过滤：只处理微信和支付宝
        if (packageName != "com.tencent.mm" && packageName != "com.eg.android.AlipayGphone") {
            return
        }

        // 避免处理自己的通知
        if (packageName == this.packageName) return

        Log.d(TAG, "收到通知事件: pkg=$packageName")

        // 从 AccessibilityEvent 中提取通知文本
        val textBuilder = StringBuilder()
        event.text?.forEach { charSequence ->
            textBuilder.append(charSequence).append(" ")
        }
        val eventText = textBuilder.toString().trim()

        // 同时尝试从窗体中提取更完整的文本
        val fullText = extractNotificationText(event) ?: eventText

        if (fullText.isBlank()) {
            Log.d(TAG, "通知文本为空，跳过")
            return
        }


        // 使用 NotificationParser 来判断是否为支付通知
        // NotificationParser 内部会检查包名和文本内容
        // 我们手动构造判断逻辑，因为 AccessibilityEvent 不是 StatusBarNotification
        val isPayment = com.example.autoexpense.util.AmountExtractor.isPaymentNotification(fullText)
        if (!isPayment) {
            Log.d(TAG, "非支付通知，跳过")
            return
        }

        // 检测支付方式
        val paymentMethod = com.example.autoexpense.util.AmountExtractor.detectPaymentMethod(packageName, fullText)

        // 检测是否是收入/退款
        val isIncome = com.example.autoexpense.util.AmountExtractor.isIncomeNotification(fullText)
        val isRefund = com.example.autoexpense.util.AmountExtractor.isRefundNotification(fullText)

        val type = when {
            isRefund -> "refund"
            isIncome -> "income"
            else -> "expense"
        }

        // 提取金额
        var amount = com.example.autoexpense.util.AmountExtractor.extract(fullText)

        // 如果是退款通知但没有金额，使用默认值并提示用户
        if (isRefund && amount == null) {
            Log.d(TAG, "退款通知但未检测到金额，使用默认值 0.0")
            amount = 0.0
        } else if (amount == null) {
            Log.d(TAG, "无法提取金额，跳过")
            return
        }

        Log.d(TAG, "检测到交易")

        // 启动 OverlayService 发 Heads-up 通知
        showExpenseConfirm(amount, paymentMethod.displayName, fullText, type)
    }

    /**
     * 从 AccessibilityEvent 尝试提取更完整的通知文本
     */
    private fun extractNotificationText(event: AccessibilityEvent): String? {
        val parts = mutableListOf<String>()

        event.text?.joinToString(" ")?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.let { parts.add(it) }

        (event.parcelableData as? Notification)?.extras?.let { extras ->
            listOf(
                Notification.EXTRA_TITLE,
                Notification.EXTRA_TEXT,
                Notification.EXTRA_BIG_TEXT,
                Notification.EXTRA_SUB_TEXT,
                Notification.EXTRA_SUMMARY_TEXT,
                Notification.EXTRA_INFO_TEXT
            ).forEach { key ->
                extras.getCharSequence(key)
                    ?.toString()
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { parts.add(it) }
            }
        }

        // 尝试从窗体内容中提取
        try {
            val rootNode = rootInActiveWindow
            if (rootNode != null) {
                val texts = mutableListOf<String>()
                extractTextFromNode(rootNode, texts)
                rootNode.recycle()
                parts.addAll(texts)
            }
        } catch (e: Exception) {
            Log.w(TAG, "提取窗体文本失败", e)
        }

        return parts
            .distinct()
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
    }

    /**
     * 递归提取节点文本
     */
    private fun extractTextFromNode(node: AccessibilityNodeInfo, texts: MutableList<String>) {
        // 获取节点文本
        node.text?.let { texts.add(it.toString()) }

        // 获取内容描述
        node.contentDescription?.let { texts.add(it.toString()) }

        // 递归子节点
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            extractTextFromNode(child, texts)
            child.recycle()
        }
    }

    /**
     * 启动 OverlayService 来发送 Heads-up 通知
     */
    private fun showExpenseConfirm(amount: Double, paymentMethod: String, rawText: String, type: String = "expense") {
        Log.d(TAG, "启动 OverlayService")
        try {
            val intent = Intent(this, OverlayService::class.java).apply {
                putExtra("amount", amount)
                putExtra("paymentMethod", paymentMethod)
                putExtra("rawText", rawText)
                putExtra("transactionType", type)
            }
            startService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "启动 OverlayService 失败", e)
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "=== 无障碍服务被中断 ===")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.w(TAG, "=== 无障碍服务已销毁 ===")
    }
}
