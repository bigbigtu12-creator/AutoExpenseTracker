package com.example.autoexpense.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.autoexpense.ui.sendConfirmedExpense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseConfirmReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "ACTION_CONFIRM_EXPENSE") return

        val amount = intent.getDoubleExtra("amount", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod") ?: "未知"
        val category = intent.getStringExtra("category") ?: "其他"
        val subCategory = intent.getStringExtra("subCategory") ?: ""
        val merchant = intent.getStringExtra("merchant") ?: ""
        val rawText = intent.getStringExtra("rawText") ?: ""
        val transactionType = intent.getStringExtra("transactionType") ?: "expense"

        scope.launch {
            val result = sendConfirmedExpense(
                context = context,
                amount = amount,
                paymentMethod = paymentMethod,
                category = category,
                subCategory = subCategory,
                merchant = merchant,
                transactionType = transactionType,
                rawText = rawText
            )

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    result.message.ifBlank { if (result.success) "记账成功" else "发送失败，请检查配置" },
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
