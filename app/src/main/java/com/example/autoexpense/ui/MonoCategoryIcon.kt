package com.example.autoexpense.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.autoexpense.ui.theme.monoPalette

private fun monoCategoryVector(name: String, income: Boolean): ImageVector {
    if (income) return when (name) {
        "工资" -> Icons.Filled.Paid
        "兼职" -> Icons.Filled.Work
        "投资理财" -> Icons.Filled.Savings
        "人情社交" -> Icons.Filled.CardGiftcard
        "报销" -> Icons.Filled.ReceiptLong
        "卖二手" -> Icons.Filled.Sell
        "生活费" -> Icons.Filled.Home
        else -> Icons.Filled.MoreHoriz
    }
    return when (name) {
        "餐饮" -> Icons.Filled.Restaurant
        "休闲娱乐" -> Icons.Filled.SportsEsports
        "购物" -> Icons.Filled.ShoppingCart
        "交通" -> Icons.Filled.DirectionsBus
        "人情社交" -> Icons.Filled.CardGiftcard
        "宠物" -> Icons.Filled.Pets
        "养娃" -> Icons.Filled.ChildCare
        "运动健身" -> Icons.Filled.FitnessCenter
        "住房" -> Icons.Filled.Home
        "爱车" -> Icons.Filled.DirectionsCar
        "医疗健康" -> Icons.Filled.LocalHospital
        "学习教育" -> Icons.Filled.MenuBook
        else -> Icons.Filled.MoreHoriz
    }
}

@Composable
fun MonoCategoryIcon(
    name: String,
    income: Boolean,
    selected: Boolean,
    size: Dp = 52.dp,
    unselectedBackground: Color = monoPalette.paperDeep
) {
    Box(
        Modifier.size(size).background(if (selected) Color(0xFFFF5A1F) else unselectedBackground, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            monoCategoryVector(name, income), contentDescription = null,
            tint = if (selected) Color.White else monoPalette.ink,
            modifier = Modifier.size(size * .55f)
        )
    }
}
