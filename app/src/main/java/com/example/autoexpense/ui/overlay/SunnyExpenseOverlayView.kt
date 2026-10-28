package com.example.autoexpense.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.data.model.Category
import com.example.autoexpense.ui.SunnyCategoryArtwork
import com.example.autoexpense.ui.SunnyAccentTicks
import com.example.autoexpense.ui.sunnyPaperGrain
import com.example.autoexpense.ui.theme.sunnyPalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val coral = Color(0xFFF45B58)
private val pink = Color(0xFFF679AE)
private val sky = Color(0xFFDCEBFB)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SunnyExpenseOverlayView(
    amount: Double,
    paymentMethod: String,
    categories: List<Category>,
    onConfirm: (String, String, String, String, Double, String) -> Unit,
    onCancel: () -> Unit,
    initialTransactionType: String = "expense"
) {
    val p = sunnyPalette
    var isIncome by remember { mutableStateOf(initialTransactionType == "income" || initialTransactionType == "refund") }
    var selectedTransactionType by remember { mutableStateOf(initialTransactionType) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCategory by remember { mutableStateOf("") }
    var customSubCategory by remember { mutableStateOf(false) }
    var merchant by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf(if (amount > 0) String.format(Locale.US, "%.2f", amount) else "") }
    var selectedPayment by remember { mutableStateOf(sunnyDefaultPayment(paymentMethod)) }
    var paymentExpanded by remember { mutableStateOf(false) }
    val displayed = remember(categories, isIncome) { sunnyCategoriesForType(categories, isIncome) }
    val dateText = remember { SimpleDateFormat("今天 HH:mm", Locale.CHINESE).format(Date()) }
    val sheetHeight = LocalConfiguration.current.screenHeightDp.dp * .80f
    val formScroll = rememberScrollState()
    LaunchedEffect(selectedCategory?.name) {
        if (selectedCategory != null) formScroll.animateScrollTo(formScroll.maxValue)
    }

    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .6f)).clickable { onCancel() },
        contentAlignment = Alignment.BottomCenter) {
        Column(
            Modifier.fillMaxWidth().height(sheetHeight)
                .background(p.paper, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .sunnyPaperGrain().clickable { }
                .padding(start = 16.dp, end = 16.dp, top = 11.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(width = 32.dp, height = 4.dp)
                .background(p.line, RoundedCornerShape(50)))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("确认记账", style = MaterialTheme.typography.headlineMedium,
                        color = p.primary, fontWeight = FontWeight.Bold)
                    SunnyAccentTicks(Modifier.padding(start = 5.dp))
                }
                Text(dateText, style = MaterialTheme.typography.labelMedium, color = p.muted)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Row(Modifier.clickable { paymentExpanded = true }.padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(if (selectedPayment.startsWith("支付宝")) "支" else if (selectedPayment.startsWith("微信")) "微" else "卡",
                            modifier = Modifier.background(if (selectedPayment.startsWith("微信")) p.green else p.primary,
                                RoundedCornerShape(5.dp)).padding(horizontal = 6.dp, vertical = 3.dp),
                            color = Color.White, style = MaterialTheme.typography.labelLarge)
                        Text("$selectedPayment ▾", style = MaterialTheme.typography.labelLarge, color = p.ink)
                    }
                    DropdownMenu(expanded = paymentExpanded, onDismissRequest = { paymentExpanded = false },
                        modifier = Modifier.background(p.card)) {
                        listOf("支付宝余额", "微信余额", "借记卡", "信用卡").forEach { method ->
                            DropdownMenuItem(text = { Text(method, color = p.ink) }, onClick = {
                                selectedPayment = method; paymentExpanded = false
                            })
                        }
                    }
                }
                Text("可修改金额", style = MaterialTheme.typography.labelMedium, color = p.muted)
            }
            OutlinedTextField(
                value = amountText,
                onValueChange = { value ->
                    val filtered = value.filter { it.isDigit() || it == '.' }
                    if (filtered.count { it == '.' } <= 1) amountText = filtered
                },
                prefix = { Text("¥ ", style = MaterialTheme.typography.headlineMedium, color = p.ink) },
                placeholder = { Text("0.00", color = p.muted) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = p.ink, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = p.primary, unfocusedBorderColor = p.line,
                    focusedContainerColor = p.card, unfocusedContainerColor = p.card,
                    focusedTextColor = p.ink, unfocusedTextColor = p.ink, cursorColor = p.primary
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SunnyTypeTab("支出", !isIncome, coral, Modifier.weight(1f)) {
                    isIncome = false; selectedTransactionType = "expense"; selectedCategory = null; selectedSubCategory = ""; customSubCategory = false
                }
                SunnyTypeTab("收入", isIncome, p.green, Modifier.weight(1f)) {
                    isIncome = true; selectedTransactionType = "income"; selectedCategory = null; selectedSubCategory = ""; customSubCategory = false
                }
            }
            Column(Modifier.weight(1f).verticalScroll(formScroll),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("选择分类", style = MaterialTheme.typography.titleMedium, color = p.primary)
                displayed.chunked(4).forEach { rowCategories ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowCategories.forEach { category ->
                            SunnyCategoryTile(category, isIncome, selectedCategory?.name == category.name,
                                Modifier.weight(1f)) { selectedCategory = category; selectedSubCategory = ""; customSubCategory = false }
                        }
                        repeat(4 - rowCategories.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
                if (selectedCategory != null) {
                    Text("子分类", style = MaterialTheme.typography.titleMedium, color = p.primary,
                        modifier = Modifier.padding(top = 5.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        selectedCategory?.subCategories.orEmpty().forEach { sub ->
                            SunnySubChip(sub.name, selectedSubCategory == sub.name && !customSubCategory) {
                                selectedSubCategory = sub.name; customSubCategory = false
                            }
                        }
                        SunnySubChip("＋ 自定义", customSubCategory) {
                            selectedSubCategory = ""; customSubCategory = true
                        }
                    }
                    if (customSubCategory) SunnyTextField(selectedSubCategory, { selectedSubCategory = it }, "输入自定义二级分类")
                }
                SunnyTextField(merchant, { merchant = it }, "商家或备注（选填）")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onCancel, modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0EDE7), contentColor = p.ink)) {
                    Text("取消", style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = {
                    onConfirm(selectedCategory?.name.orEmpty(), selectedSubCategory.trim(), merchant.trim(),
                        selectedTransactionType,
                        amountText.toDoubleOrNull() ?: 0.0, selectedPayment)
                }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = p.primary, contentColor = Color.White)) {
                    Text("确认发送", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SunnyTypeTab(label: String, selected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.height(46.dp).background(if (selected) color.copy(alpha = .12f) else Color(0xFFF0EDE7),
        RoundedCornerShape(11.dp)).border(if (selected) 1.5.dp else 0.dp, if (selected) color else Color.Transparent,
        RoundedCornerShape(11.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(label, style = MaterialTheme.typography.titleMedium,
            color = if (selected) color else sunnyPalette.ink)
    }
}

@Composable
private fun SunnyCategoryTile(category: Category, income: Boolean, selected: Boolean,
    modifier: Modifier, onClick: () -> Unit) {
    val (icon, color) = sunnyCategoryVisual(category.name, income)
    Column(
        modifier.height(82.dp).background(if (selected) sky else sunnyPalette.card, RoundedCornerShape(11.dp))
            .border(if (selected) 1.5.dp else 1.dp, if (selected) sunnyPalette.primary else sunnyPalette.line.copy(alpha = .7f),
                RoundedCornerShape(11.dp)).clickable { onClick() }.padding(4.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!SunnyCategoryArtwork(category.name, income)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(29.dp))
        }
        Text(category.name, style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
            color = sunnyPalette.ink, maxLines = 1, overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 3.dp))
    }
}

@Composable
private fun SunnySubChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(label, modifier = Modifier.background(if (selected) sky else Color(0xFFF0EDE7), RoundedCornerShape(20.dp))
        .border(if (selected) 1.dp else 0.dp, if (selected) sunnyPalette.primary else Color.Transparent,
            RoundedCornerShape(20.dp)).clickable { onClick() }.padding(horizontal = 11.dp, vertical = 7.dp),
        style = MaterialTheme.typography.labelLarge, color = sunnyPalette.ink)
}

@Composable
private fun SunnyTextField(value: String, onChange: (String) -> Unit, label: String) {
    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) },
        modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(11.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = sunnyPalette.primary, unfocusedBorderColor = sunnyPalette.line,
            focusedTextColor = sunnyPalette.ink, unfocusedTextColor = sunnyPalette.ink,
            focusedContainerColor = sunnyPalette.card, unfocusedContainerColor = sunnyPalette.card,
            focusedLabelColor = sunnyPalette.primary, unfocusedLabelColor = sunnyPalette.muted,
            cursorColor = sunnyPalette.primary
        ))
}

private fun sunnyDefaultPayment(source: String) = when {
    source.contains("信用") -> "信用卡"
    source.contains("借记") || source.contains("银行") -> "借记卡"
    source.contains("微信") -> "微信余额"
    else -> "支付宝余额"
}

private fun sunnyCategoriesForType(categories: List<Category>, income: Boolean): List<Category> {
    val incomeNames = setOf("工资", "兼职", "投资理财", "报销", "卖二手", "生活费")
    val incomeSubs = setOf("月薪", "奖金", "补贴", "临时工", "自由职业", "基金", "股票", "利息", "分红",
        "转账", "差旅", "办公", "闲鱼", "转转", "父母", "配偶", "退款", "中奖", "其他收入")
    val filtered = categories.filter { category ->
        val isIncome = category.name in incomeNames || category.subCategories.any { it.name in incomeSubs }
        isIncome == income
    }.distinctBy { it.name }
    return if (filtered.isEmpty()) listOf(Category("其他")) else filtered
}

internal fun sunnyCategoryVisual(name: String, income: Boolean): Pair<ImageVector, Color> {
    if (income) return when (name) {
        "工资" -> Icons.Filled.Paid to coral
        "兼职" -> Icons.Filled.Work to sunnyPalette.primary
        "投资理财" -> Icons.Filled.Savings to sunnyPalette.green
        "人情社交" -> Icons.Filled.CardGiftcard to pink
        "报销" -> Icons.Filled.ReceiptLong to sunnyPalette.primary
        "卖二手" -> Icons.Filled.Sell to sunnyPalette.secondary
        "生活费" -> Icons.Filled.Home to coral
        else -> Icons.Filled.MoreHoriz to sunnyPalette.primary
    }
    return when (name) {
        "餐饮" -> Icons.Filled.Restaurant to sunnyPalette.secondary
        "休闲娱乐" -> Icons.Filled.SportsEsports to sunnyPalette.green
        "购物" -> Icons.Filled.ShoppingBag to coral
        "交通" -> Icons.Filled.DirectionsBus to sunnyPalette.primary
        "人情社交" -> Icons.Filled.CardGiftcard to pink
        "宠物" -> Icons.Filled.Pets to coral
        "养娃" -> Icons.Filled.ChildCare to pink
        "运动健身" -> Icons.Filled.FitnessCenter to sunnyPalette.green
        "住房" -> Icons.Filled.Home to sunnyPalette.secondary
        "爱车" -> Icons.Filled.DirectionsCar to sunnyPalette.primary
        "医疗健康" -> Icons.Filled.LocalHospital to Color(0xFF87B8EE)
        "学习教育" -> Icons.Filled.School to sunnyPalette.primary
        else -> Icons.Filled.MoreHoriz to Color(0xFFA9BFD5)
    }
}
