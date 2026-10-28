package com.example.autoexpense.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.autoexpense.R
import com.example.autoexpense.ui.sunnyPaperGrain
import com.example.autoexpense.data.local.CategoryRepository
import com.example.autoexpense.data.model.Category
import com.example.autoexpense.data.model.SubCategory
import com.example.autoexpense.ui.SunnyCategoryArtwork
import com.example.autoexpense.ui.FolkCategoryArtwork
import com.example.autoexpense.ui.MonoCategoryIcon
import com.example.autoexpense.ui.isIncomeCategory
import com.example.autoexpense.ui.overlay.sunnyCategoryVisual
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.LocalAppSkin
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.currentSkinPalette
import com.example.autoexpense.ui.theme.RetroPanel
import com.example.autoexpense.ui.theme.RetroPrimaryButton
import com.example.autoexpense.ui.theme.RetroSecondaryButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryEditorScreen(
    onNavigateBack: () -> Unit
) {
    val palette = currentSkinPalette()
    val sunny = LocalAppSkin.current != AppSkin.Caramel
    val folk = LocalAppSkin.current == AppSkin.Folk
    val mono = LocalAppSkin.current == AppSkin.Mono
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categoryRepository = remember { CategoryRepository(context) }
    val categories by categoryRepository.getCategories().collectAsState(initial = emptyList())
    var newCategoryName by remember { mutableStateOf("") }
    var showAddCategory by remember { mutableStateOf(false) }
    var showIncome by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            if (mono) TopAppBar(
                title = { Text("分类管理", color = palette.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = palette.ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = palette.background)
            ) else if (sunny) Box(Modifier.fillMaxWidth().height(122.dp).background(palette.paper)
                .then(if (folk) Modifier else Modifier.sunnyPaperGrain())) {
                Image(painterResource(if (folk) R.drawable.folk_header_art else R.drawable.sunny_riso_hero), null, contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.BottomEnd).size(width = 170.dp, height = 97.dp))
                Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = palette.primary,
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 18.dp, top = 8.dp)
                        .size(28.dp).clickable { onNavigateBack() })
                Column(Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 8.dp)) {
                    Text("分类管理", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.Black, color = if (folk) palette.ink else palette.primary)
                    Box(Modifier.size(width = 43.dp, height = 5.dp).background(palette.secondary, RoundedCornerShape(8.dp)))
                    Text("管理你的收支分类", style = MaterialTheme.typography.bodyMedium, color = if (folk) palette.ink else palette.primary)
                }
            } else TopAppBar(
                title = { Text("分类管理", color = palette.header) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = palette.header)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = palette.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .then(if (sunny && !folk && !mono) Modifier.sunnyPaperGrain() else Modifier)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (sunny) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(false to "支出", true to "收入").forEach { (income, label) ->
                    Box(Modifier.weight(1f).height(46.dp)
                        .background(if (showIncome == income) {
                            if (mono) palette.secondary else if (income) palette.green.copy(alpha = .14f) else Color(0xFFF45B58).copy(alpha = .13f)
                        } else palette.card, RoundedCornerShape(12.dp))
                        .border(if (mono) 0.dp else 1.dp, if (showIncome == income) {
                            if (mono) palette.secondary else if (income) palette.green else Color(0xFFF45B58)
                        } else palette.line, RoundedCornerShape(12.dp))
                        .clickable { showIncome = income }, contentAlignment = Alignment.Center) {
                        Text(label, style = MaterialTheme.typography.titleMedium,
                            color = if (showIncome == income) {
                                if (mono) Color.White else if (income) palette.green else Color(0xFFF45B58)
                            } else palette.ink)
                    }
                }
            }
            if (folk || mono) {
                Box(Modifier.fillMaxWidth().height(51.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (mono) palette.paperDeep else palette.primary).clickable { showAddCategory = !showAddCategory },
                    contentAlignment = Alignment.Center) {
                    Text("＋  新增一级分类", color = if (mono) palette.ink else Color.White,
                        style = MaterialTheme.typography.titleMedium)
                }
            }
            if ((!folk && !mono) || showAddCategory) RetroPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("新增一级分类", style = MaterialTheme.typography.titleLarge, color = palette.ink)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RetroTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = "例如：咖啡",
                            modifier = Modifier.weight(1f)
                        )
                        RetroPrimaryButton(
                            onClick = {
                                if (newCategoryName.isNotBlank()) {
                                    scope.launch {
                                        categoryRepository.saveCategories(categories + Category(
                                            newCategoryName.trim(), transactionType = if (showIncome) "income" else "expense"
                                        ))
                                        newCategoryName = ""
                                        showAddCategory = false
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            val preferredExpense = listOf("餐饮", "购物", "交通", "宠物", "住房", "医疗健康", "休闲娱乐",
                "人情社交", "养娃", "运动健身", "爱车", "学习教育", "其他")
            val preferredIncome = listOf("工资", "兼职", "投资理财", "人情社交", "报销", "卖二手", "生活费", "其他")
            val preferred = if (mono && showIncome) preferredIncome else preferredExpense
            val visibleCategories = if (folk || mono) categories.withIndex().sortedBy { entry ->
                val rank = preferred.indexOf(entry.value.name)
                if (rank < 0) 100 + entry.index else rank
            } else categories.withIndex().toList()
            visibleCategories.forEach { (index, category) ->
                if (sunny && (if (mono) isIncomeCategory(categories, index) else sunnyCategoryIsIncome(category)) != showIncome) return@forEach
                CategoryItem(
                    category = category,
                    sunny = sunny,
                    monoIncome = showIncome,
                    onDelete = {
                        scope.launch {
                            val updated = categories.toMutableList().apply { removeAt(index) }
                            categoryRepository.saveCategories(updated)
                        }
                    },
                    onUpdate = { updatedCategory ->
                        scope.launch {
                            val updated = categories.toMutableList().apply { set(index, updatedCategory) }
                            categoryRepository.saveCategories(updated)
                        }
                    }
                )
            }

            RetroSecondaryButton(
                onClick = {
                    scope.launch {
                        categoryRepository.saveCategories(categoryRepository.getDefaultCategories())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("恢复默认分类")
            }
        }
    }
}

private fun sunnyCategoryIsIncome(category: Category): Boolean {
    category.transactionType?.let { return it == "income" }
    if (category.name in setOf("工资", "兼职", "投资理财", "报销", "卖二手", "生活费")) return true
    val markers = setOf("月薪", "奖金", "补贴", "临时工", "自由职业", "基金", "股票", "利息", "分红",
        "差旅", "办公", "闲鱼", "转转", "父母", "配偶", "退款", "中奖", "其他收入")
    return category.subCategories.any { it.name in markers }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryItem(
    category: Category,
    sunny: Boolean,
    monoIncome: Boolean,
    onDelete: () -> Unit,
    onUpdate: (Category) -> Unit
) {
    val palette = currentSkinPalette()
    if (LocalAppSkin.current == AppSkin.Mono) {
        MonoCategoryItem(category, monoIncome, onDelete, onUpdate)
        return
    }
    var newSubName by remember { mutableStateOf("") }
    var addingSubCategory by remember { mutableStateOf(false) }
    val folk = LocalAppSkin.current == AppSkin.Folk

    RetroPanel(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                if (sunny) {
                    val income = category.name in setOf("工资", "兼职", "投资理财", "报销", "卖二手", "生活费")
                    val (icon, color) = sunnyCategoryVisual(category.name, income)
                    val drawn = if (LocalAppSkin.current == AppSkin.Folk)
                        FolkCategoryArtwork(category.name, income, size = 44.dp)
                    else SunnyCategoryArtwork(category.name, income, size = 38.dp)
                    if (!drawn) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(34.dp))
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(category.name, style = MaterialTheme.typography.titleLarge, color = palette.ink)
                    if (!folk) Text(
                        text = "${category.subCategories.size} 个子分类",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.muted
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .then(if (folk) Modifier else Modifier.clip(CircleShape)
                            .background(palette.paper).border(1.dp, palette.line, CircleShape))
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "删除", tint = androidx.compose.ui.graphics.Color(0xFFF45B58))
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                category.subCategories.forEach { sub ->
                    Text(
                        text = sub.name,
                        modifier = Modifier
                            .background(palette.paperDeep, RoundedCornerShape(10.dp))
                            .border(1.dp, palette.line.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 11.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.ink
                    )
                }
                if (folk) Text("＋ 添加子分类",
                    modifier = Modifier.border(1.dp, palette.line, RoundedCornerShape(10.dp))
                        .clickable { addingSubCategory = !addingSubCategory }
                        .padding(horizontal = 11.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium, color = palette.ink)
            }

            if (!folk || addingSubCategory) Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RetroTextField(
                    value = newSubName,
                    onValueChange = { newSubName = it },
                    label = "新增子分类",
                    modifier = Modifier.weight(1f)
                )
                RetroSecondaryButton(
                    onClick = {
                        if (newSubName.isNotBlank()) {
                            val newSubs = category.subCategories + SubCategory(newSubName.trim())
                            onUpdate(category.copy(subCategories = newSubs))
                            newSubName = ""
                            addingSubCategory = false
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MonoCategoryItem(category: Category, income: Boolean, onDelete: () -> Unit, onUpdate: (Category) -> Unit) {
    val palette = currentSkinPalette()
    var expanded by remember { mutableStateOf(false) }
    var newSubName by remember { mutableStateOf("") }
    var addingSub by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().background(palette.paperDeep, RoundedCornerShape(15.dp))
        .padding(horizontal = 12.dp, vertical = 9.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(Modifier.fillMaxWidth().clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            MonoCategoryIcon(category.name, income, expanded, size = 46.dp,
                unselectedBackground = Color(0xFFF7F7F7))
            Column(Modifier.weight(1f)) {
                Text(category.name, color = palette.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${category.subCategories.size} 个子分类", color = palette.muted, fontSize = 12.sp)
            }
            Icon(if (expanded) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowRight,
                contentDescription = if (expanded) "收起" else "展开", tint = palette.ink)
        }
        if (expanded) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)) {
                category.subCategories.forEach { sub ->
                    Row(Modifier.background(Color.White, RoundedCornerShape(12.dp))
                        .padding(start = 10.dp, end = 5.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(sub.name, color = palette.ink, fontSize = 13.sp)
                        Icon(Icons.Outlined.Delete, contentDescription = "删除${sub.name}", tint = palette.muted,
                            modifier = Modifier.padding(start = 8.dp).size(19.dp).clickable {
                                onUpdate(category.copy(subCategories = category.subCategories.filterNot { it.name == sub.name }))
                            })
                    }
                }
                Text("＋ 添加子分类", color = palette.ink, fontSize = 13.sp,
                    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { addingSub = !addingSub }.padding(horizontal = 10.dp, vertical = 7.dp))
            }
            if (addingSub) Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RetroTextField(newSubName, { newSubName = it }, "新增子分类", Modifier.weight(1f))
                RetroPrimaryButton(onClick = {
                    if (newSubName.isNotBlank()) {
                        onUpdate(category.copy(subCategories = category.subCategories + SubCategory(newSubName.trim())))
                        newSubName = ""; addingSub = false
                    }
                }) { Icon(Icons.Outlined.Add, contentDescription = "添加", modifier = Modifier.size(18.dp)) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("删除一级分类", color = palette.secondary, fontSize = 13.sp,
                    modifier = Modifier.clickable { onDelete() }.padding(8.dp))
            }
        }
    }
}

@Composable
private fun RetroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val palette = currentSkinPalette()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.line.copy(alpha = 0.6f),
            focusedTextColor = palette.ink,
            unfocusedTextColor = palette.ink,
            focusedLabelColor = palette.muted,
            unfocusedLabelColor = palette.muted,
            cursorColor = palette.primary,
            focusedContainerColor = palette.paper,
            unfocusedContainerColor = palette.paper
        )
    )
}

