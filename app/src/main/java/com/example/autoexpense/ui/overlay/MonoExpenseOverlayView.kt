package com.example.autoexpense.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.data.model.Category
import com.example.autoexpense.ui.MonoCategoryIcon
import com.example.autoexpense.ui.isIncomeCategory
import com.example.autoexpense.ui.theme.monoPalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val orange = Color(0xFFFF5A1F)
private val black = Color(0xFF111111)
private val gray = Color(0xFFE9E9E8)
private val softOrange = Color(0xFFFFE4D4)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonoExpenseOverlayView(
    amount: Double,
    paymentMethod: String,
    categories: List<Category>,
    onConfirm: (String, String, String, String, Double, String) -> Unit,
    onCancel: () -> Unit,
    initialTransactionType: String = "expense"
) {
    var isIncome by remember { mutableStateOf(initialTransactionType == "income" || initialTransactionType == "refund") }
    var transactionType by remember { mutableStateOf(initialTransactionType) }
    var chosen by remember { mutableStateOf<Category?>(null) }
    var subcategory by remember { mutableStateOf("") }
    var customSub by remember { mutableStateOf(false) }
    var merchant by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf(if (amount > 0) String.format(Locale.US, "%.2f", amount) else "") }
    var payment by remember(paymentMethod) { mutableStateOf(monoDefaultPayment(paymentMethod)) }
    var paymentExpanded by remember { mutableStateOf(false) }
    val shown = remember(categories, isIncome) { monoCategories(categories, isIncome) }
    val dateText = remember { SimpleDateFormat("今天 HH:mm", Locale.CHINESE).format(Date()) }
    val height = LocalConfiguration.current.screenHeightDp.dp * .90f

    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .60f)).clickable { onCancel() },
        contentAlignment = Alignment.BottomCenter) {
        Column(
            Modifier.fillMaxWidth().height(height)
                .clip(RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp))
                .background(monoPalette.background).clickable { }
                .padding(start = 18.dp, end = 18.dp, top = 9.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(width = 38.dp, height = 4.dp)
                .background(Color(0xFFBBBBBB), RoundedCornerShape(10.dp)))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("确认记账", color = black, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(dateText, color = monoPalette.muted, fontSize = 13.sp)
            }

            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box {
                    Row(Modifier.fillMaxWidth().height(48.dp).background(gray, RoundedCornerShape(13.dp))
                        .clickable { paymentExpanded = true }.padding(horizontal = 13.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("付款来源", color = monoPalette.muted, fontSize = 13.sp)
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.size(27.dp).background(if (payment.startsWith("微信")) Color(0xFF1AAD19) else Color(0xFF1677FF),
                            RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                            Text(if (payment.startsWith("支付宝")) "支" else if (payment.startsWith("微信")) "微" else "卡",
                                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(payment, color = black, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 7.dp).weight(1f))
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "选择付款来源", tint = monoPalette.muted)
                    }
                    DropdownMenu(expanded = paymentExpanded, onDismissRequest = { paymentExpanded = false },
                        modifier = Modifier.width((LocalConfiguration.current.screenWidthDp - 36).dp)
                            .background(Color.White)) {
                        listOf("支付宝余额", "微信余额", "借记卡", "信用卡").forEach { item ->
                            DropdownMenuItem(text = {
                                Text(if (item == payment) "$item  ✓" else item,
                                    color = if (item == payment) orange else black, fontSize = 16.sp)
                            }, onClick = { payment = item; paymentExpanded = false })
                        }
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¥", color = black, fontSize = 33.sp, fontWeight = FontWeight.Black)
                        BasicTextField(
                            value = amountText,
                            onValueChange = { next ->
                                val filtered = next.filter { it.isDigit() || it == '.' }
                                if (filtered.count { it == '.' } <= 1) amountText = filtered
                            },
                            modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = black, fontSize = 47.sp,
                                fontWeight = FontWeight.Black),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(orange),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            decorationBox = { inner ->
                                Box { if (amountText.isEmpty()) Text("0.00", color = monoPalette.muted,
                                    fontSize = 47.sp, fontWeight = FontWeight.Black)
                                    inner() }
                            }
                        )
                    }
                    Box(Modifier.fillMaxWidth().height(3.dp).background(orange, RoundedCornerShape(4.dp)))
                }

                Row(Modifier.fillMaxWidth().height(46.dp).background(gray, RoundedCornerShape(25.dp))) {
                    MonoSegment("支出", !isIncome, Modifier.weight(1f)) {
                        isIncome = false; transactionType = "expense"; chosen = null; subcategory = ""; customSub = false
                    }
                    MonoSegment("收入", isIncome, Modifier.weight(1f)) {
                        isIncome = true; transactionType = "income"; chosen = null; subcategory = ""; customSub = false
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("选择分类", color = black, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    shown.chunked(4).forEach { group ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            group.forEach { item ->
                                MonoCategoryTile(item, isIncome, chosen?.name == item.name, Modifier.weight(1f)) {
                                    chosen = item; subcategory = ""; customSub = false
                                }
                            }
                            repeat(4 - group.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }

                if (chosen != null) {
                    Text("二级分类", color = black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        chosen?.subCategories.orEmpty().forEach { sub ->
                            MonoChip(sub.name, !customSub && subcategory == sub.name) {
                                subcategory = sub.name; customSub = false
                            }
                        }
                        MonoChip("＋ 自定义", customSub) { customSub = true; subcategory = "" }
                    }
                    if (customSub) MonoInput(subcategory, { subcategory = it }, "输入自定义二级分类")
                }
                MonoInput(merchant, { merchant = it }, "商家或备注（选填）")
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onCancel, modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = gray, contentColor = black)) {
                    Text("取消", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = {
                    onConfirm(chosen?.name.orEmpty(), subcategory.trim(), merchant.trim(),
                        transactionType, amountText.toDoubleOrNull() ?: 0.0, payment)
                }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = black, contentColor = Color.White)) {
                    Text("确认发送", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MonoSegment(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.fillMaxSize().clip(RoundedCornerShape(25.dp))
        .background(if (selected) orange else Color.Transparent).clickable { onClick() },
        contentAlignment = Alignment.Center) {
        Text(label, color = if (selected) Color.White else monoPalette.muted,
            fontSize = 16.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun MonoCategoryTile(category: Category, income: Boolean, selected: Boolean,
    modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clickable { onClick() }.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        MonoCategoryIcon(category.name, income, selected, size = 52.dp)
        Text(category.name, color = if (selected) orange else black, fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MonoChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(label, modifier = Modifier.background(if (selected) softOrange else gray, RoundedCornerShape(25.dp))
        .clickable { onClick() }.padding(horizontal = 13.dp, vertical = 9.dp),
        color = black, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
}

@Composable
private fun MonoInput(value: String, onChange: (String) -> Unit, hint: String) {
    Row(Modifier.fillMaxWidth().height(49.dp).background(gray, RoundedCornerShape(12.dp))
        .padding(horizontal = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.ReceiptLong, null, tint = monoPalette.muted, modifier = Modifier.size(21.dp))
        BasicTextField(value = value, onValueChange = onChange, singleLine = true,
            modifier = Modifier.weight(1f).padding(start = 9.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = black, fontSize = 15.sp),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(orange),
            decorationBox = { inner -> Box { if (value.isEmpty()) Text(hint, color = monoPalette.muted, fontSize = 14.sp); inner() } })
    }
}

private fun monoDefaultPayment(source: String): String = when {
    source.contains("信用") -> "信用卡"
    source.contains("借记") || source.contains("银行") -> "借记卡"
    source.contains("微信") -> "微信余额"
    else -> "支付宝余额"
}

private fun monoCategories(categories: List<Category>, income: Boolean): List<Category> {
    val preferredExpense = listOf("餐饮", "购物", "交通", "宠物", "住房", "医疗健康", "运动健身",
        "休闲娱乐", "人情社交", "养娃", "爱车", "学习教育", "其他")
    val preferredIncome = listOf("工资", "兼职", "投资理财", "人情社交", "报销", "卖二手", "生活费", "其他")
    val preferred = if (income) preferredIncome else preferredExpense
    return categories.withIndex().filter { isIncomeCategory(categories, it.index) == income }
        .map { it.value }.distinctBy { it.name }
        .sortedWith(compareBy { category -> preferred.indexOf(category.name).takeIf { it >= 0 } ?: 100 })
        .ifEmpty { listOf(Category("其他")) }
}
