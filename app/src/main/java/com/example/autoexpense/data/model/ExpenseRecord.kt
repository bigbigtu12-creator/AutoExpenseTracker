package com.example.autoexpense.data.model

/**
 * 一条记账记录
 */
data class ExpenseRecord(
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val category: String,
    val subCategory: String,
    val merchant: String,
    val timestamp: Long = System.currentTimeMillis()
)
