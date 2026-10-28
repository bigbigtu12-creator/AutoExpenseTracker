package com.example.autoexpense.util

import com.example.autoexpense.data.model.PaymentMethod
import java.util.regex.Pattern

/**
 * 金额提取器
 * 从通知文本中提取支付金额
 */
object AmountExtractor {

    // 同一条通知包含多笔明细时，先取明确标注的总付款金额。
    private val totalAmountPattern = Pattern.compile(
        "(?:合并付款|合并支付|多笔订单).{0,80}?(?:总计|合计|总付款金额|实付款|实付|共支付)[:：\\s]*[¥￥]?(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)"
    )

    // 金额匹配模式
    private val amountPatterns = listOf(
        // ¥123.45 或 ￥123.45
        Pattern.compile("[¥￥]\\s*(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)"),
        // 金额: 123.45 / 到账金额 123.45 / 退款金额 123.45
        Pattern.compile("(?:金额|到账金额|退款金额|收款金额)[:：]?\\s*(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)"),
        // 17.5元将退到微信零钱 / 17.5元已到账
        Pattern.compile("(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)\\s*元\\D{0,18}(?:退到|退回|到账|入账|微信零钱|余额)"),
        // 已退款14.8元 / 退款 14.8 元 / 到账 14.8 元 / 收到 14.8 元
        Pattern.compile("(?:退款|已退款|退回|到账|入账|收款|收到|转入)\\D{0,18}(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)\\s*元"),
        // 123.45元
        Pattern.compile("(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)\\s*元")
    )

    // 支出通知关键词
    private val expensePatterns = listOf(
        Pattern.compile("支付(?:成功|完成)"),
        Pattern.compile("付款(?:成功|完成)"),
        Pattern.compile("消费"),
        Pattern.compile("已支付"),
        Pattern.compile("支出")
    )

    // 收入通知关键词（更严谨）
    private val incomePatterns = listOf(
        Pattern.compile("收款到账"),
        Pattern.compile("收到.*转账"),
        Pattern.compile("到账"),
        Pattern.compile("入账"),
        Pattern.compile("已到账"),
        Pattern.compile("转账到账"),
        Pattern.compile("转入"),
        Pattern.compile("已收款"),
        Pattern.compile("收款成功"),
        Pattern.compile("收入")
    )

    // 退款通知关键词
    private val refundPatterns = listOf(
        Pattern.compile("退款到账"),
        Pattern.compile("退款成功"),
        Pattern.compile("已退款"),
        Pattern.compile("退款.*退到"),
        Pattern.compile("退回"),
        Pattern.compile("将退到"),
        Pattern.compile("退款到账通知"),
        Pattern.compile("退款提醒"),  // 支付宝：退款提醒
        Pattern.compile("收到.*退款")  // 支付宝：你收到一笔XX元退款
    )

    /**
     * 从文本中提取金额
     * @return 金额，如果无法解析返回 null
     */
    fun extract(text: String): Double? {
        val totalMatcher = totalAmountPattern.matcher(text)
        if (totalMatcher.find()) {
            totalMatcher.group(1)?.replace(",", "")?.toDoubleOrNull()?.let { return it }
        }
        for (pattern in amountPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace(",", "")
                val amount = amountStr?.toDoubleOrNull()
                return amount
            }
        }
        android.util.Log.d("AmountExtractor", "无法提取金额")
        return null
    }

    /**
     * 根据包名和文本判断支付方式
     */
    fun detectPaymentMethod(packageName: String, text: String): PaymentMethod {
        return when {
            packageName == "com.tencent.mm" ||
            text.contains("微信支付") ||
            text.contains("微信") -> PaymentMethod.WECHAT

            packageName == "com.eg.android.AlipayGphone" ||
            text.contains("支付宝") ||
            text.contains("Alipay") -> PaymentMethod.ALIPAY

            else -> PaymentMethod.UNKNOWN
        }
    }

    /**
     * 判断是否是支付/收款相关通知
     * 使用更严谨的模式匹配，避免聊天消息误触发
     */
    fun isPaymentNotification(text: String): Boolean {
        // 检查是否匹配支出或收入模式
        val isExpense = expensePatterns.any { it.matcher(text).find() }
        val isIncome = incomePatterns.any { it.matcher(text).find() }
        val isRefund = refundPatterns.any { it.matcher(text).find() }

        // 如果是退款通知，即使没有金额格式也要触发（因为通知可能被折叠）
        if (isRefund) {
            android.util.Log.d("AmountExtractor", "检测到退款通知")
            return true
        }

        // 其他类型必须包含金额格式
        val hasAmount = amountPatterns.any { it.matcher(text).find() }
        if (!hasAmount) {
            android.util.Log.d("AmountExtractor", "无金额格式，跳过")
            return false
        }

        val result = isExpense || isIncome
        android.util.Log.d("AmountExtractor", "检查通知: isExpense=$isExpense, isIncome=$isIncome, 结果=$result")
        return result
    }

    /**
     * 判断是否是收入通知
     */
    fun isIncomeNotification(text: String): Boolean {
        val result = incomePatterns.any { it.matcher(text).find() }
        android.util.Log.d("AmountExtractor", "检查收入通知: $result")
        return result
    }

    /**
     * 判断是否是退款通知
     */
    fun isRefundNotification(text: String): Boolean {
        val result = refundPatterns.any { it.matcher(text).find() }
        android.util.Log.d("AmountExtractor", "检查退款通知: $result")
        return result
    }
}
