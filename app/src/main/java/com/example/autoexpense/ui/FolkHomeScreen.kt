package com.example.autoexpense.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.R
import com.example.autoexpense.ui.theme.folkPalette

private val folkCoral = Color(0xFFF5544D)
private val folkPink = Color(0xFFEE77A0)
private val folkShape = RoundedCornerShape(24.dp)

@Composable
fun FolkHomeScreen(onNavigateToSettings: () -> Unit, onManualEntry: () -> Unit, onChangeSkin: () -> Unit) {
    val p = folkPalette
    Column(Modifier.fillMaxSize().background(p.paper).verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.fillMaxWidth().height(132.dp)) {
            Image(painterResource(R.drawable.folk_header_art), null, contentScale = ContentScale.Fit,
                modifier = Modifier.align(Alignment.TopEnd).size(width = 154.dp, height = 117.dp))
            Text("✹", color = folkCoral, fontSize = 35.sp,
                modifier = Modifier.align(Alignment.TopStart).offset(y = 82.dp))
            Column(Modifier.align(Alignment.TopStart), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("AUTO LEDGER", color = p.ink, fontWeight = FontWeight.Black, fontSize = 30.sp, lineHeight = 34.sp)
                Text("账本正在后台守候", color = p.ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Icon(Icons.Filled.Settings, contentDescription = "设置", tint = p.ink,
                modifier = Modifier.align(Alignment.TopEnd).size(38.dp).clickable { onNavigateToSettings() })
        }

        Column(Modifier.fillMaxWidth().clip(folkShape).background(p.card)
            .border(1.dp, p.line, folkShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✹", color = folkCoral, fontSize = 29.sp, modifier = Modifier.padding(end = 7.dp))
                Text("自动记账", color = p.ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
            Text("支付后自动识别，确认后发送到你的记账接收地址", color = p.ink,
                style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FolkStatus(Modifier.weight(1f), Icons.Filled.Circle, "服务运行中", "等待支付通知", p.green, Color(0xFFE7F5EA))
                FolkStatus(Modifier.weight(1f), Icons.Filled.Link, "接收地址", "确认后发送", p.ink, Color(0xFFFFE7E2))
            }
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                FolkAction(Modifier.weight(1f), Icons.Filled.Edit, "手动记账", p.primary, Color.White, onManualEntry)
                FolkAction(Modifier.weight(1f), Icons.Filled.Settings, "设置", p.secondary, p.ink, onNavigateToSettings)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            FolkFlow(Modifier.weight(1f), Icons.Filled.ReceiptLong, "识别", "微信·支付宝通知", Color(0xFFFFE5DF), folkCoral)
            FolkFlow(Modifier.weight(1f), Icons.Filled.CheckCircle, "确认", "分类与备注", Color(0xFFE3EDFF), p.primary)
            FolkFlow(Modifier.weight(1f), Icons.Filled.Send, "发送", "接收地址处理", Color(0xFFE5F5EA), p.green)
        }
        Row(Modifier.fillMaxWidth().clip(folkShape).background(p.card).border(1.dp, p.line, folkShape)
            .clickable { onChangeSkin() }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✿", fontSize = 28.sp, color = folkPink)
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text("换皮肤", color = p.ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("当前：花漾平涂", color = p.muted, fontSize = 13.sp)
            }
            Text("›", color = p.primary, fontSize = 26.sp)
        }
        Box(Modifier.fillMaxWidth().clip(folkShape).background(p.card).border(1.dp, p.line, folkShape)
            .height(120.dp)) {
            Row(Modifier.align(Alignment.TopStart).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("✿", color = p.secondary, fontSize = 27.sp)
                Column(Modifier.padding(start = 9.dp)) {
                    Text("自动识别 + 手动补录", color = p.ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("让每一笔支出都不被遗漏，记账更轻松。", color = p.muted, fontSize = 13.sp)
                }
            }
            Image(painterResource(R.drawable.folk_bottom_art), null, contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().height(58.dp).align(Alignment.BottomCenter).offset(y = 12.dp))
        }
    }
}

@Composable
private fun FolkStatus(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, tint: Color, background: Color) {
    Row(modifier.clip(RoundedCornerShape(16.dp)).background(background).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(25.dp))
        Column {
            Text(title, color = folkPalette.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(subtitle, color = folkPalette.muted, fontSize = 11.sp, maxLines = 1)
        }
    }
}

@Composable
private fun FolkAction(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String, background: Color, foreground: Color, onClick: () -> Unit) {
    Row(modifier.height(58.dp).clip(RoundedCornerShape(15.dp)).background(background)
        .clickable { onClick() }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = foreground, modifier = Modifier.size(26.dp))
        Text(label, color = foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp))
        Text("  ›", color = foreground, fontSize = 21.sp)
    }
}

@Composable
private fun FolkFlow(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, background: Color, accent: Color) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(background).padding(horizontal = 3.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(31.dp))
        Text(title, color = folkPalette.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = folkPalette.muted, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}
