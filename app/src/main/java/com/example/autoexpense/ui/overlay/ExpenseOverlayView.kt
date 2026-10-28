package com.example.autoexpense.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BabyChangingStation
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.autoexpense.data.model.Category
import com.example.autoexpense.R
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.RetroPrimaryButton
import com.example.autoexpense.ui.theme.RetroSecondaryButton
import com.example.autoexpense.ui.theme.TornPaperShape
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.LocalAppSkin
import com.example.autoexpense.ui.theme.TornPaperShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.abs

private enum class LedgerType(val value: String, val title: String, val subtitle: String) {
    Expense("expense", "支出", "付款记录"),
    Income("income", "收入", "到账记录")
}

private val TicketPaper = Color(0xFFFFF3DD)
private val TicketPanel = Color(0xFFF4E4CC)
private val TicketBrown = Color(0xFF422310)
private val TicketOrange = Color(0xFFC45A25)
private val TicketGold = Color(0xFFFFB934)
private val TicketLine = Color(0xFFCBAE8C)
private val TicketMuted = Color(0xFF80654B)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpenseOverlayView(
    amount: Double,
    paymentMethod: String,
    categories: List<Category>,
    onConfirm: (category: String, subCategory: String, merchant: String, transactionType: String, confirmedAmount: Double, selectedPaymentMethod: String) -> Unit,
    onCancel: () -> Unit,
    onManageCategories: () -> Unit = {},
    initialTransactionType: String = "expense"
) {
    if (LocalAppSkin.current == AppSkin.Mono) {
        MonoExpenseOverlayView(amount, paymentMethod, categories, onConfirm, onCancel, initialTransactionType)
        return
    }
    if (LocalAppSkin.current == AppSkin.Folk) {
        FolkExpenseOverlayView(amount, paymentMethod, categories, onConfirm, onCancel, initialTransactionType)
        return
    }
    if (LocalAppSkin.current == AppSkin.Sunny) {
        SunnyExpenseOverlayView(amount, paymentMethod, categories, onConfirm, onCancel, initialTransactionType)
        return
    }
    var ledgerType by remember {
        mutableStateOf(if (initialTransactionType == "income" || initialTransactionType == "refund") LedgerType.Income else LedgerType.Expense)
    }
    var selectedTransactionType by remember {
        mutableStateOf(if (initialTransactionType == "refund") "refund" else ledgerType.value)
    }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCategory by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf(if (amount > 0.0) String.format("%.2f", amount) else "") }
    var selectedPaymentMethod by remember(paymentMethod) { mutableStateOf(defaultPaymentMethod(paymentMethod)) }
    var showCustomSubCategory by remember { mutableStateOf(false) }
    var customSubCategory by remember { mutableStateOf("") }
    val formScroll = rememberScrollState()

    LaunchedEffect(selectedCategory?.name) {
        if (selectedCategory != null) {
            delay(180)
            formScroll.animateScrollTo(formScroll.maxValue)
        }
    }

    val visibleCategories = remember(categories, ledgerType) {
        categoriesForType(categories, ledgerType).ifEmpty { listOf(Category("其他")) }
    }
    val currentDate = remember {
        SimpleDateFormat("今天 HH:mm", Locale.CHINESE).format(Date())
    }
    val amountColor = TicketBrown
    val sheetHeight = LocalConfiguration.current.screenHeightDp.dp * 0.78f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.62f))
            .clickable { onCancel() },
        verticalArrangement = Arrangement.Bottom
    ) {
        TicketSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
                .clickable { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(formScroll),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    TicketHeader(
                        amountInput = amountInput,
                        onAmountChange = { amountInput = it },
                        amountColor = amountColor,
                        paymentMethod = selectedPaymentMethod,
                        onPaymentMethodChange = { selectedPaymentMethod = it },
                        currentDate = currentDate
                    )

                    DottedDivider()

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LedgerTypeCard(
                            type = LedgerType.Expense,
                            selected = ledgerType == LedgerType.Expense,
                            accent = TicketOrange,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                ledgerType = LedgerType.Expense
                                selectedTransactionType = LedgerType.Expense.value
                                selectedCategory = null
                                selectedSubCategory = ""
                                customSubCategory = ""
                                showCustomSubCategory = false
                            }
                        )
                        LedgerTypeCard(
                            type = LedgerType.Income,
                            selected = ledgerType == LedgerType.Income,
                            accent = TicketBrown,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                ledgerType = LedgerType.Income
                                selectedTransactionType = LedgerType.Income.value
                                selectedCategory = null
                                selectedSubCategory = ""
                                customSubCategory = ""
                                showCustomSubCategory = false
                            }
                        )
                    }

                    Text("选择分类", style = MaterialTheme.typography.titleMedium, color = TicketBrown)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((((visibleCategories.size + 3) / 4 * 86) + (((visibleCategories.size + 3) / 4 - 1) * 7)).dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        items(visibleCategories) { category ->
                            CategoryTile(
                                category = category,
                                selected = selectedCategory?.name == category.name,
                                type = ledgerType,
                                icon = iconForCategory(category.name),
                                color = TicketOrange,
                                onClick = {
                                    selectedCategory = category
                                    selectedSubCategory = ""
                                    customSubCategory = ""
                                    showCustomSubCategory = false
                                }
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = selectedCategory != null,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedCategory?.subCategories.orEmpty().forEach { sub ->
                                    SubCategoryChip(
                                        name = sub.name,
                                        selected = selectedSubCategory == sub.name && !showCustomSubCategory,
                                        onClick = {
                                            selectedSubCategory = sub.name
                                            customSubCategory = ""
                                            showCustomSubCategory = false
                                        }
                                    )
                                }
                                AddSubCategoryChip(
                                    selected = showCustomSubCategory,
                                    onClick = {
                                        showCustomSubCategory = true
                                        selectedSubCategory = customSubCategory
                                    }
                                )
                            }

                            AnimatedVisibility(visible = showCustomSubCategory) {
                                ReceiptTextField(
                                    value = customSubCategory,
                                    onValueChange = {
                                        customSubCategory = it
                                        selectedSubCategory = it
                                    },
                                    label = "输入自定义二级分类",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    ReceiptTextField(
                        value = merchant,
                        onValueChange = { merchant = it },
                        label = "商家或备注",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RetroSecondaryButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("取消", maxLines = 1)
                    }
                    androidx.compose.material3.Button(
                        onClick = {
                            onConfirm(
                                selectedCategory?.name.orEmpty(),
                                selectedSubCategory.trim(),
                                merchant.trim(),
                                selectedTransactionType,
                                amountInput.toAmountOrZero(),
                                selectedPaymentMethod
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = TicketOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(11.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, TicketGold)
                    ) {
                        Text("➤  确认发送", maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(TornPaperShape)
            .background(TicketPaper, TornPaperShape)
    ) {
        DotMatrixBackground()
        content()
    }
}

@Composable
private fun DotMatrixBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 13.dp.toPx()
        val radius = 0.9.dp.toPx()
        var y = 22.dp.toPx()
        while (y < size.height - 8.dp.toPx()) {
            var x = 10.dp.toPx()
            while (x < size.width - 10.dp.toPx()) {
                drawCircle(
                    color = TicketBrown.copy(alpha = 0.035f),
                    radius = radius,
                    center = Offset(x, y)
                )
                x += step
            }
            y += step
        }
    }
}

@Composable
private fun TicketHeader(
    amountInput: String,
    onAmountChange: (String) -> Unit,
    amountColor: Color,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    currentDate: String
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(width = 30.dp, height = 5.dp).background(TicketLine, RoundedCornerShape(50)))
        }
        Text("确认记账", style = MaterialTheme.typography.headlineMedium,
            color = TicketBrown, fontWeight = FontWeight.Black)
        Row(
            modifier = Modifier.fillMaxWidth().background(RetroColors.Card, RoundedCornerShape(10.dp))
                .padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box {
                Row(
                    modifier = Modifier.clickable { expanded = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        Modifier.size(28.dp).background(paymentColor(paymentMethod), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (paymentMethod.startsWith("支付宝")) "支" else if (paymentMethod.startsWith("微信")) "微" else "卡",
                            color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                    Text(paymentMethod, style = MaterialTheme.typography.labelLarge, color = TicketBrown, maxLines = 1)
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "选择付款方式",
                        tint = TicketBrown, modifier = Modifier.size(17.dp))
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(TicketPaper).border(1.dp, TicketLine, RoundedCornerShape(8.dp))
                ) {
                    listOf("支付宝余额", "微信余额", "借记卡", "信用卡").forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            onPaymentMethodChange(option)
                            expanded = false
                        })
                    }
                }
            }
            Text(currentDate, style = MaterialTheme.typography.labelMedium, color = TicketMuted,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1)
        BasicTextField(
            value = amountInput,
            onValueChange = { value ->
                val filtered = value.filter { it.isDigit() || it == '.' }
                if (filtered.count { it == '.' } <= 1) {
                    onAmountChange(filtered)
                }
            },
            modifier = Modifier.width(118.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                color = amountColor,
                textAlign = TextAlign.End
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "¥",
                        style = MaterialTheme.typography.headlineMedium,
                        color = amountColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (amountInput.isEmpty()) {
                            Text(
                                text = "0.00",
                                style = MaterialTheme.typography.headlineMedium,
                                color = amountColor.copy(alpha = 0.45f),
                                textAlign = TextAlign.End
                            )
                        }
                        innerTextField()
                    }
                    Icon(Icons.Outlined.Edit, contentDescription = "修改金额", tint = amountColor,
                        modifier = Modifier.size(14.dp))
                }
            }
        )
        }
    }
}

private fun defaultPaymentMethod(source: String): String = when {
    source.contains("信用") -> "信用卡"
    source.contains("借记") || source.contains("银行") -> "借记卡"
    source.contains("微信") -> "微信余额"
    else -> "支付宝余额"
}

private fun paymentColor(method: String): Color = when {
    method.startsWith("支付宝") -> Color(0xFF1685E9)
    method.startsWith("微信") -> Color(0xFF43A75B)
    method == "信用卡" -> Color(0xFFAB6138)
    else -> Color(0xFF527B92)
}

@Composable
private fun DottedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
    ) {
        drawLine(
            color = TicketLine.copy(alpha = 0.45f),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
        drawCircle(
            color = TicketLine.copy(alpha = 0.32f),
            radius = 5f,
            center = Offset(0f, size.height / 2f),
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = TicketLine.copy(alpha = 0.32f),
            radius = 5f,
            center = Offset(size.width, size.height / 2f),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
private fun LedgerTypeCard(
    type: LedgerType,
    selected: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val background = if (selected) accent else TicketPanel.copy(alpha = 0.6f)
    val border = if (selected) TicketGold else TicketLine

    Row(
        modifier = modifier
            .background(background, RoundedCornerShape(8.dp))
            .border(if (selected) 2.dp else 1.dp, border, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(if (selected) TicketPaper else TicketBrown, CircleShape)
                .border(1.dp, border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (type == LedgerType.Expense) "-" else "+",
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) accent else TicketPaper
            )
        }
        Column {
            Text(type.title, style = MaterialTheme.typography.titleMedium, color = if (selected) Color.White else TicketBrown)
        }
    }
}

@Composable
private fun CategoryTile(
    category: Category,
    selected: Boolean,
    type: LedgerType,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val background = if (selected) TicketOrange else TicketPanel.copy(alpha = 0.55f)
    val context = LocalContext.current
    val stickerSheet = remember {
        android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.category_sticker_sheet)
    }
    val stickerIndex = stickerIndex(category.name, type)

    Column(
        modifier = Modifier
            .background(background, RoundedCornerShape(8.dp))
            .then(if (selected) Modifier.border(2.dp, TicketGold, RoundedCornerShape(8.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 3.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stickerIndex != null && stickerSheet != null) {
            val painter = remember(stickerSheet, stickerIndex) {
                BitmapPainter(extractSticker(stickerSheet, stickerIndex).asImageBitmap())
            }
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(width = 69.dp, height = 61.dp)
            )
        } else {
            Box(
                modifier = Modifier.size(width = 69.dp, height = 61.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(49.dp))
            }
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else TicketBrown,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Extracts a complete sticker from the approved atlas, then builds one solid cut edge and shadow. */
private fun extractSticker(sheet: android.graphics.Bitmap, index: Int): android.graphics.Bitmap {
    val column = index % 4
    val row = index / 4
    val top = intArrayOf(120, 340, 560, 778, 1065, 1286)[row]
    val width = 216
    val height = intArrayOf(132, 137, 133, 123, 133, 130)[row]
    val pixels = IntArray(width * height)
    sheet.getPixels(pixels, 0, width, 32 + column * 244, top, width, height)
    val background = pixels[20 * width + 20]
    fun isBackground(pixel: Int): Boolean {
        val dr = android.graphics.Color.red(pixel) - android.graphics.Color.red(background)
        val dg = android.graphics.Color.green(pixel) - android.graphics.Color.green(background)
        val db = android.graphics.Color.blue(pixel) - android.graphics.Color.blue(background)
        return dr * dr + dg * dg + db * db < 53 * 53
    }
    val queue = IntArray(pixels.size)
    val visited = BooleanArray(pixels.size)
    var head = 0
    var tail = 0
    fun enqueue(position: Int) {
        if (!visited[position] && isBackground(pixels[position])) {
            visited[position] = true
            queue[tail++] = position
        }
    }
    for (x in 0 until width) {
        enqueue(x)
        enqueue((height - 1) * width + x)
    }
    for (y in 0 until height) {
        enqueue(y * width)
        enqueue(y * width + width - 1)
    }
    while (head < tail) {
        val position = queue[head++]
        val x = position % width
        val y = position / width
        pixels[position] = android.graphics.Color.TRANSPARENT
        if (x > 0) enqueue(position - 1)
        if (x < width - 1) enqueue(position + 1)
        if (y > 0) enqueue(position - width)
        if (y < height - 1) enqueue(position + width)
    }
    val rawDrawing = BooleanArray(pixels.size) { position ->
        val x = position % width
        val y = position / width
        val pixel = pixels[position]
        if (pixel == android.graphics.Color.TRANSPARENT || x < 20 || x > width - 21 || y < 5 || y > height - 10) false else {
            val r = android.graphics.Color.red(pixel)
            val g = android.graphics.Color.green(pixel)
            val b = android.graphics.Color.blue(pixel)
            val high = maxOf(r, g, b)
            val low = minOf(r, g, b)
            (high < 178 && low < 160) || (high - low > 70 && low < 215)
        }
    }
    // Discard tiny printed flecks from the atlas, keeping all real icon pieces.
    val drawing = BooleanArray(pixels.size)
    val componentSeen = BooleanArray(pixels.size)
    for (start in rawDrawing.indices) {
        if (!rawDrawing[start] || componentSeen[start]) continue
        head = 0; tail = 0
        queue[tail++] = start
        componentSeen[start] = true
        while (head < tail) {
            val p = queue[head++]
            val x = p % width
            val y = p / width
            fun visit(n: Int) {
                if (rawDrawing[n] && !componentSeen[n]) { componentSeen[n] = true; queue[tail++] = n }
            }
            if (x > 0) visit(p - 1)
            if (x < width - 1) visit(p + 1)
            if (y > 0) visit(p - width)
            if (y < height - 1) visit(p + width)
        }
        if (tail >= 40) for (i in 0 until tail) drawing[queue[i]] = true
    }
    var silhouette = drawing.copyOf()
    repeat(8) {
        val expanded = silhouette.copyOf()
        for (y in 1 until height - 1) for (x in 1 until width - 1) {
            val p = y * width + x
            if (silhouette[p]) {
                expanded[p - 1] = true
                expanded[p + 1] = true
                expanded[p - width] = true
                expanded[p + width] = true
            }
        }
        silhouette = expanded
    }
    // Make the white area inside closed outlines opaque as well.
    val outside = BooleanArray(pixels.size)
    head = 0; tail = 0
    fun offerOutside(p: Int) {
        if (!silhouette[p] && !outside[p]) { outside[p] = true; queue[tail++] = p }
    }
    for (x in 0 until width) { offerOutside(x); offerOutside((height - 1) * width + x) }
    for (y in 0 until height) { offerOutside(y * width); offerOutside(y * width + width - 1) }
    while (head < tail) {
        val p = queue[head++]
        val x = p % width
        val y = p / width
        if (x > 0) offerOutside(p - 1)
        if (x < width - 1) offerOutside(p + 1)
        if (y > 0) offerOutside(p - width)
        if (y < height - 1) offerOutside(p + width)
    }
    if (index !in setOf(1, 2, 7, 12)) {
        for (i in silhouette.indices) if (!outside[i]) silhouette[i] = true
    }

    var minX = width; var minY = height; var maxX = 0; var maxY = 0
    for (i in silhouette.indices) if (silhouette[i]) {
        val x = i % width; val y = i / width
        minX = minOf(minX, x); minY = minOf(minY, y)
        maxX = maxOf(maxX, x); maxY = maxOf(maxY, y)
    }
    if (minX > maxX || minY > maxY) {
        return android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
    }
    val margin = 10
    val outWidth = maxX - minX + 1 + margin * 2
    val outHeight = maxY - minY + 1 + margin * 2
    val output = IntArray(outWidth * outHeight)
    // Soft translucent drop shadow follows the actual die-cut shape.
    for (y in minY..maxY) for (x in minX..maxX) {
        if (!silhouette[y * width + x]) continue
        val ox = x - minX + margin
        val oy = y - minY + margin + 3
        for (dy in -3..3) for (dx in -3..3) {
            if (dx * dx + dy * dy > 10) continue
            val p = (oy + dy) * outWidth + ox + dx
            output[p] = android.graphics.Color.argb(38, 61, 38, 23)
        }
    }
    for (y in minY..maxY) for (x in minX..maxX) {
        val p = y * width + x
        if (!silhouette[p]) continue
        output[(y - minY + margin) * outWidth + x - minX + margin] =
            if (drawing[p]) pixels[p] or -0x1000000 else android.graphics.Color.rgb(255, 250, 236)
    }
    return android.graphics.Bitmap.createBitmap(output, outWidth, outHeight, android.graphics.Bitmap.Config.ARGB_8888)
}

private fun stickerIndex(name: String, type: LedgerType): Int? {
    val names = if (type == LedgerType.Expense) {
        listOf("餐饮", "休闲娱乐", "购物", "交通", "人情社交", "宠物", "养娃", "运动健身", "住房", "爱车", "医疗健康", "学习教育", "其他")
    } else {
        listOf("工资", "兼职", "投资理财", "人情社交", "报销", "卖二手", "生活费", "其他")
    }
    val index = names.indexOf(name)
    return if (index < 0) null else if (type == LedgerType.Expense) index else index + 16
}

@Composable
private fun SubCategoryChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = name,
        modifier = Modifier
            .background(if (selected) TicketOrange else TicketPanel, RoundedCornerShape(12.dp))
            .border(if (selected) 2.dp else 1.dp, if (selected) TicketGold else TicketLine, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) Color.White else TicketBrown,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
    )
}

@Composable
private fun AddSubCategoryChip(
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = "+ 自定义",
        modifier = Modifier
            .background(if (selected) TicketOrange else TicketPanel, RoundedCornerShape(12.dp))
            .border(if (selected) 2.dp else 1.dp, if (selected) TicketGold else TicketLine, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) Color.White else TicketBrown,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ReceiptTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TicketOrange,
            unfocusedBorderColor = TicketLine,
            focusedTextColor = TicketBrown,
            unfocusedTextColor = TicketBrown,
            focusedLabelColor = TicketMuted,
            unfocusedLabelColor = TicketMuted,
            cursorColor = TicketOrange,
            focusedContainerColor = TicketPanel.copy(alpha = 0.28f),
            unfocusedContainerColor = TicketPanel.copy(alpha = 0.28f)
        )
    )
}

private fun categoriesForType(categories: List<Category>, type: LedgerType): List<Category> {
    val distinct = categories.distinctBy { category ->
        category.name + category.subCategories.joinToString { it.name }
    }
    return when (type) {
        LedgerType.Income -> distinct.filter { it.isIncomeCategory() }.distinctBy { it.name }
        LedgerType.Expense -> distinct.filterNot { it.isIncomeCategory() }.distinctBy { it.name }
    }
}

private fun Category.isIncomeCategory(): Boolean {
    transactionType?.let { return it == "income" }
    val incomeNames = setOf("工资", "兼职", "投资理财", "报销", "卖二手", "生活费")
    val incomeSubNames = setOf(
        "月薪",
        "奖金",
        "补贴",
        "临时工",
        "自由职业",
        "基金",
        "股票",
        "利息",
        "分红",
        "转账",
        "差旅",
        "办公",
        "闲鱼",
        "转转",
        "父母",
        "配偶",
        "退款",
        "中奖",
        "其他收入"
    )
    return name in incomeNames || subCategories.any { it.name in incomeSubNames }
}

private fun iconForCategory(name: String): ImageVector = when {
    name.contains("早餐") || name.contains("餐") -> Icons.Outlined.Restaurant
    name.contains("买菜") || name.contains("菜") -> Icons.Outlined.LocalGroceryStore
    name.contains("饮料") || name.contains("咖啡") -> Icons.Outlined.LocalCafe
    name.contains("娱乐") || name.contains("电影") -> Icons.Outlined.Movie
    name.contains("游戏") -> Icons.Outlined.SportsEsports
    name.contains("购物") -> Icons.Outlined.ShoppingBag
    name.contains("衣") || name.contains("美妆") -> Icons.Outlined.Checkroom
    name.contains("交通") || name.contains("公交") || name.contains("地铁") -> Icons.Outlined.DirectionsBus
    name.contains("火车") || name.contains("高铁") -> Icons.Outlined.Train
    name.contains("社交") || name.contains("礼物") || name.contains("红包") -> Icons.Outlined.CardGiftcard
    name.contains("宠物") -> Icons.Outlined.Pets
    name.contains("养娃") -> Icons.Outlined.ChildCare
    name.contains("奶粉") || name.contains("尿布") -> Icons.Outlined.BabyChangingStation
    name.contains("运动") || name.contains("健身") -> Icons.Outlined.FitnessCenter
    name.contains("住房") -> Icons.Outlined.Home
    name.contains("车") || name.contains("加油") || name.contains("停车") -> Icons.Outlined.DirectionsCar
    name.contains("医疗") || name.contains("健康") -> Icons.Outlined.LocalHospital
    name.contains("教育") || name.contains("学习") || name.contains("课程") -> Icons.Outlined.School
    name.contains("日用") || name.contains("清洁") -> Icons.Outlined.CleaningServices
    name.contains("工资") -> Icons.Outlined.Paid
    name.contains("兼职") -> Icons.Outlined.AttachMoney
    name.contains("理财") || name.contains("投资") -> Icons.Outlined.Savings
    name.contains("报销") -> Icons.Outlined.AccountBalance
    name.contains("二手") -> Icons.Outlined.Sell
    name.contains("生活费") -> Icons.Outlined.Redeem
    name.contains("人情") -> Icons.Outlined.FavoriteBorder
    name.contains("运动") -> Icons.Outlined.SportsBasketball
    name.contains("其他") -> Icons.Outlined.MoreHoriz
    else -> Icons.Outlined.Category
}

private fun accentForCategory(name: String): Color = when (abs(name.hashCode()) % 4) {
    0 -> RetroColors.Red
    1 -> RetroColors.Gold
    2 -> RetroColors.Green
    else -> RetroColors.Blue
}

private fun String.toAmountOrZero(): Double {
    return trim().toDoubleOrNull() ?: 0.0
}
