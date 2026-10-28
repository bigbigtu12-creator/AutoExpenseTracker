package com.example.autoexpense.util

import android.app.Notification
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.autoexpense.data.model.PaymentMethod

/**
 * 通知解析器
 * 解析微信/支付宝的支付通知
 */
object NotificationParser {

    private const val TAG = "NotificationParser"

    // 目标应用包名
    private val targetPackages = setOf(
        "com.tencent.mm",           // 微信
        "com.eg.android.AlipayGphone" // 支付宝
    )

    /**
     * 解析通知，返回金额和支付方式
     * 如果不是支付通知，返回 null
     */
    data class ParseResult(
        val amount: Double,
        val paymentMethod: PaymentMethod,
        val rawText: String
    )

    fun parse(sbn: StatusBarNotification): ParseResult? {
        val packageName = sbn.packageName
        if (packageName !in targetPackages) return null

        // 获取通知文本
        val title = sbn.notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = getNotificationText(sbn.notification) ?: ""
        val fullText = "$title $text"



        // 判断是否是支付通知
        val isPayment = AmountExtractor.isPaymentNotification(fullText)
        Log.d(TAG, "isPaymentNotification=$isPayment")
        if (!isPayment) {
            Log.d(TAG, "非支付通知，跳过")
            return null
        }

        // 提取金额
        val isRefund = AmountExtractor.isRefundNotification(fullText)
        val amount = AmountExtractor.extract(fullText)
        if (amount == null && !isRefund) {
            Log.d(TAG, "无法解析金额")
            return null
        }
        // 退款通知允许金额为 null（通知可能被折叠），使用 0.0 作为默认值
        val finalAmount = amount ?: 0.0

        // 判断支付方式
        val paymentMethod = AmountExtractor.detectPaymentMethod(packageName, fullText)

        return ParseResult(finalAmount, paymentMethod, fullText)
    }

    /**
     * 从通知对象中提取完整文本
     */
    private fun getNotificationText(notification: Notification): String? {
        val extras = notification.extras

        // 优先获取大文本
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
        if (!bigText.isNullOrEmpty()) {
            return bigText.toString()
        }

        // 普通文本
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)
        if (!text.isNullOrEmpty()) {
            return text.toString()
        }

        // 摘要文本
        val summary = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)
        if (!summary.isNullOrEmpty()) {
            return summary.toString()
        }

        return null
    }
}
