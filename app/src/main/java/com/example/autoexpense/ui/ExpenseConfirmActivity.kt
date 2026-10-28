package com.example.autoexpense.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.autoexpense.data.local.CategoryRepository
import com.example.autoexpense.data.local.SettingsDataStore
import com.example.autoexpense.data.remote.FeishuApiService
import com.example.autoexpense.ui.overlay.ExpenseOverlayView
import com.example.autoexpense.ui.theme.AutoExpenseRetroTheme
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.LocalAppSkin
import com.example.autoexpense.util.AmountExtractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class ExpenseConfirmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amount = intent.getDoubleExtra("amount", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod") ?: "未知"
        val rawText = intent.getStringExtra("rawText") ?: ""
        val transactionType = intent.getStringExtra("transactionType") ?: "expense"
        val displayAmount = if (amount > 0.0) amount else AmountExtractor.extract(rawText) ?: 0.0

        setContent {
            val settings = remember { SettingsDataStore(this) }
            val skinValue by settings.appSkin.collectAsState(initial = "caramel")
            CompositionLocalProvider(LocalAppSkin provides AppSkin.from(skinValue)) {
            AutoExpenseRetroTheme {
                ExpenseConfirmDialog(
                    amount = displayAmount,
                    paymentMethod = paymentMethod,
                    rawText = rawText,
                    transactionType = transactionType,
                    onDismiss = { finish() }
                )
            }
            }
        }
    }
}

@Composable
fun ExpenseConfirmDialog(
    amount: Double,
    paymentMethod: String,
    rawText: String,
    transactionType: String = "expense",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categoryRepository = remember { CategoryRepository(context) }
    val categories by categoryRepository.getCategories().collectAsState(initial = emptyList())
    val requestId = remember { UUID.randomUUID().toString() }
    var isSending by remember { mutableStateOf(false) }

    ExpenseOverlayView(
        amount = amount,
        paymentMethod = paymentMethod,
        categories = categories,
        initialTransactionType = transactionType,
        onConfirm = { category, subCategory, merchant, selectedTransactionType, confirmedAmount, selectedPaymentMethod ->
            if (isSending) return@ExpenseOverlayView
            if (category.isEmpty()) {
                Toast.makeText(context, "请选择分类", Toast.LENGTH_SHORT).show()
                return@ExpenseOverlayView
            }
            if (confirmedAmount <= 0.0) {
                Toast.makeText(context, "金额无效", Toast.LENGTH_SHORT).show()
                return@ExpenseOverlayView
            }

            scope.launch {
                isSending = true
                val result = sendConfirmedExpense(
                    context = context,
                    amount = confirmedAmount,
                    paymentMethod = selectedPaymentMethod,
                    category = category,
                    subCategory = subCategory,
                    merchant = merchant,
                    transactionType = selectedTransactionType,
                    rawText = rawText,
                    requestId = requestId
                )
                if (result.success) {
                    try {
                        categoryRepository.rememberSubCategory(category, subCategory, selectedTransactionType)
                    } catch (e: Exception) {
                        android.util.Log.e("ExpenseConfirm", "保存二级分类失败", e)
                    }
                }
                Toast.makeText(
                    context,
                    result.message.ifBlank { if (result.success) "记账成功" else "发送失败，请检查配置" },
                    Toast.LENGTH_LONG
                ).show()
                isSending = false
                if (result.success) onDismiss()
            }
        },
        onCancel = onDismiss
    )
}

suspend fun sendConfirmedExpense(
    context: Context,
    amount: Double,
    paymentMethod: String,
    category: String,
    subCategory: String,
    merchant: String,
    transactionType: String,
    rawText: String,
    requestId: String = UUID.randomUUID().toString()
): FeishuApiService.SendResult {
    val settings = SettingsDataStore(context)
    val sendMode = settings.sendMode.first()
    val webhookUrl = settings.webhookUrl.first()

    return when (sendMode) {
        "bitable", "both" -> {
            val bitableResult = FeishuApiService.writeExpenseToBitableDetailed(
                config = FeishuApiService.BitableConfig(
                    appId = settings.feishuAppId.first(),
                    appSecret = settings.feishuAppSecret.first(),
                    appToken = settings.bitableAppToken.first(),
                    tableId = settings.bitableTableId.first()
                ),
                amount = amount,
                paymentMethod = paymentMethod,
                category = category,
                subCategory = subCategory,
                merchant = merchant,
                transactionType = transactionType,
                rawText = rawText,
                requestId = requestId
            )
            if (!bitableResult.success) return bitableResult

            if (sendMode == "both") {
                val notifyWebhook = webhookUrl
                if (notifyWebhook.isNotBlank()) {
                    val notifyResult = FeishuApiService.sendExpenseDetailed(
                        webhookUrl = notifyWebhook,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        category = category,
                        subCategory = subCategory,
                        merchant = merchant,
                        transactionType = transactionType
                    )
                    if (!notifyResult.success) {
                        return FeishuApiService.SendResult(
                            true,
                            "\u5df2\u5199\u5165\u8868\u683c\uff0c\u4f46\u7fa4\u901a\u77e5\u53d1\u9001\u5931\u8d25"
                        )
                    }
                }
            }
            bitableResult
        }

        else -> {
            if (webhookUrl.isBlank()) {
                FeishuApiService.SendResult(false, "\u8bf7\u5148\u914d\u7f6e\u98de\u4e66 Webhook")
            } else {
                FeishuApiService.sendExpenseDetailed(
                    webhookUrl = webhookUrl,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    category = category,
                    subCategory = subCategory,
                    merchant = merchant,
                    transactionType = transactionType
                )
            }
        }
    }
}
