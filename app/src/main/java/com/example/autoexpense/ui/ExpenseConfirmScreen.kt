package com.example.autoexpense.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.autoexpense.data.local.CategoryRepository
import com.example.autoexpense.ui.overlay.ExpenseOverlayView
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.currentSkinPalette
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.LocalAppSkin
import com.example.autoexpense.util.AmountExtractor
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseConfirmScreen(
    amount: Double,
    paymentMethod: String,
    rawText: String,
    onBack: () -> Unit
) {
    val palette = currentSkinPalette()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categoryRepository = remember { CategoryRepository(context) }
    val categories by categoryRepository.getCategories().collectAsState(initial = emptyList())
    val displayAmount = remember(amount, rawText) {
        if (amount > 0.0) amount else AmountExtractor.extract(rawText) ?: 0.0
    }
    val requestId = remember { UUID.randomUUID().toString() }
    var isSending by remember { mutableStateOf(false) }

    val overlay: @Composable () -> Unit = {
            ExpenseOverlayView(
                amount = displayAmount,
                paymentMethod = paymentMethod,
                categories = categories,
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
                        if (result.success) onBack()
                    }
                },
                onCancel = onBack
            )
    }
    if (LocalAppSkin.current != AppSkin.Caramel) {
        Box(Modifier.fillMaxSize()) {
            if (LocalAppSkin.current == AppSkin.Mono)
                MonoHomeScreen(onNavigateToSettings = {}, onManualEntry = {}, onChangeSkin = {})
            else if (LocalAppSkin.current == AppSkin.Folk)
                FolkHomeScreen(onNavigateToSettings = {}, onManualEntry = {}, onChangeSkin = {})
            else SunnyHomeScreen(onNavigateToSettings = {}, onManualEntry = {}, onChangeSkin = {})
            overlay()
        }
    } else {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                TopAppBar(
                    title = { Text("确认记账", color = palette.header) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回", tint = palette.header)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = palette.background)
                )
            }
        ) { paddingValues ->
            Box(Modifier.fillMaxSize().background(palette.background).padding(paddingValues)) { overlay() }
        }
    }
}
