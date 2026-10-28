package com.example.autoexpense.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.autoexpense.R
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.sunnyPalette
import com.example.autoexpense.ui.theme.folkPalette
import com.example.autoexpense.ui.theme.monoPalette

@Composable
fun SkinPickerScreen(selectedSkin: AppSkin, onSelect: (AppSkin) -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(RetroColors.Ink)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = RetroColors.Paper,
                modifier = Modifier.size(32.dp).clickable { onBack() })
            Text("换皮肤", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = RetroColors.Paper)
        }
        Text("选择你喜欢的记账界面", fontSize = 16.sp,
            color = RetroColors.PaperDeep)
        SkinOption(AppSkin.Caramel, selectedSkin == AppSkin.Caramel, onSelect)
        SkinOption(AppSkin.Sunny, selectedSkin == AppSkin.Sunny, onSelect)
        SkinOption(AppSkin.Folk, selectedSkin == AppSkin.Folk, onSelect)
        SkinOption(AppSkin.Mono, selectedSkin == AppSkin.Mono, onSelect)
        Text("当前使用：${selectedSkin.displayName}", fontSize = 14.sp,
            color = RetroColors.PaperDeep)
    }
}

@Composable
private fun SkinOption(skin: AppSkin, selected: Boolean, onSelect: (AppSkin) -> Unit) {
    val sunny = skin == AppSkin.Sunny
    val folk = skin == AppSkin.Folk
    val mono = skin == AppSkin.Mono
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(RetroColors.Paper, RoundedCornerShape(17.dp))
            .border(if (selected) 2.dp else 1.dp,
                if (selected) RetroColors.Gold else RetroColors.Line, RoundedCornerShape(17.dp))
            .clickable { onSelect(skin) }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(82.dp).background(if (mono) monoPalette.paper else if (folk) folkPalette.paper else if (sunny) sunnyPalette.paper else Color(0xFF362217), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (mono) {
                Text("47.56", color = monoPalette.ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp)
                    .size(width = 52.dp, height = 3.dp).background(monoPalette.secondary))
            } else if (folk) {
                Image(painterResource(R.drawable.folk_header_art), null, contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp))
            } else if (sunny) {
                Box(Modifier.align(Alignment.TopEnd).padding(9.dp).size(28.dp).background(sunnyPalette.secondary, CircleShape))
                Box(Modifier.align(Alignment.BottomStart).padding(9.dp).size(width = 58.dp, height = 25.dp)
                    .background(sunnyPalette.primary, RoundedCornerShape(22.dp)))
                Box(Modifier.align(Alignment.TopStart).padding(12.dp).size(15.dp).background(Color(0xFFF679AE), CircleShape))
            } else {
                Image(painterResource(R.drawable.toffee_detective), contentDescription = null, modifier = Modifier.size(70.dp))
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(skin.displayName, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = RetroColors.Ink)
            Text(if (mono) "黑白高对比 · 橙色焦点" else if (folk) "花朵星芒 · 平涂色块 · 暖白" else if (sunny) "蓝黄孔版 · 扁平图标 · 纸张" else "暖褐色 · 撕纸 · 贴纸图标",
                fontSize = 13.sp, color = RetroColors.Muted)
        }
        Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            if (selected) Icon(Icons.Outlined.CheckCircle, contentDescription = "使用中", tint = RetroColors.Red)
        }
    }
}

