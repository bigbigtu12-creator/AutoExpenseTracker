package com.example.autoexpense.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AmountExtractorTest {

    @Test
    fun wechatBalancePaymentIsNotTreatedAsRefund() {
        val text = "微信支付 支付成功 ￥28.40 微信零钱支付"

        assertTrue(AmountExtractor.isPaymentNotification(text))
        assertFalse(AmountExtractor.isRefundNotification(text))
        assertFalse(AmountExtractor.isIncomeNotification(text))
        assertEquals(28.40, AmountExtractor.extract(text) ?: 0.0, 0.001)
    }

    @Test
    fun refundToWechatBalanceKeepsRefundTypeAndAmount() {
        val text = "已退款 17.5元将退到微信零钱"

        assertTrue(AmountExtractor.isPaymentNotification(text))
        assertTrue(AmountExtractor.isRefundNotification(text))
        assertEquals(17.50, AmountExtractor.extract(text) ?: 0.0, 0.001)
    }

    @Test
    fun incomingPaymentIsRecognized() {
        val text = "微信支付 收款到账 金额：88.00元"

        assertTrue(AmountExtractor.isPaymentNotification(text))
        assertTrue(AmountExtractor.isIncomeNotification(text))
        assertFalse(AmountExtractor.isRefundNotification(text))
        assertEquals(88.00, AmountExtractor.extract(text) ?: 0.0, 0.001)
    }

    @Test
    fun combinedPaymentUsesExplicitTotal() {
        val text = "合并付款 小红书订单 ¥200.55、¥6.02、¥11.75，实付 ¥218.32 支付成功"
        assertEquals(218.32, AmountExtractor.extract(text) ?: 0.0, 0.001)
    }

}
