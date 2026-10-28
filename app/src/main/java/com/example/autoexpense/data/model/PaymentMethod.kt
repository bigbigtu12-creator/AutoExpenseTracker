package com.example.autoexpense.data.model

/**
 * 支付方式枚举
 */
enum class PaymentMethod(val displayName: String) {
    WECHAT("微信支付"),     // 微信
    ALIPAY("支付宝"),       // 支付宝
    UNKNOWN("未知")         // 未知
}
