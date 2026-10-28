package com.example.autoexpense.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import com.example.autoexpense.R
import com.example.autoexpense.ui.theme.sunnyPalette

private val pink = Color(0xFFF679AE)
private val cardShape = RoundedCornerShape(16.dp)

@Composable
fun SunnyHomeScreen(onNavigateToSettings: () -> Unit, onManualEntry: () -> Unit, onChangeSkin: () -> Unit) {
    val p = sunnyPalette
    BoxWithConstraints(Modifier.fillMaxSize().background(p.paper)) {
        SunnyPaperTexture()
        val heroHeight = (maxHeight * .40f).coerceIn(290.dp, 345.dp)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.fillMaxWidth().height(115.dp)) {
                Image(painterResource(R.drawable.sunny_riso_hero), null, contentScale = ContentScale.FillBounds,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(y = 45.dp)
                        .size(width = 190.dp, height = 105.dp))
                Column(Modifier.align(Alignment.TopStart), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("自动记账", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                        color = p.primary, fontWeight = FontWeight.Black)
                    Box(Modifier.size(width = 46.dp, height = 5.dp).background(p.secondary, RoundedCornerShape(8.dp)))
                    Text("账本正在后台守候", style = MaterialTheme.typography.titleMedium,
                        color = p.primary, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Filled.Settings, contentDescription = "设置", tint = p.primary,
                    modifier = Modifier.align(Alignment.TopEnd).size(36.dp).clickable { onNavigateToSettings() })
            }
            Box(Modifier.fillMaxWidth().height(heroHeight).clip(cardShape).background(p.card)
                .border(1.dp, p.line.copy(alpha = .7f), cardShape)) {
                SunnyPaperTexture()
                Image(painterResource(R.drawable.sunny_riso_hero), null, contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().height(155.dp).align(Alignment.BottomCenter)
                        .offset(y = 20.dp))
                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("自动记账", style = MaterialTheme.typography.headlineMedium,
                            color = p.primary, fontWeight = FontWeight.Black)
                        SunnyAccentTicks(Modifier.padding(start = 6.dp))
                    }
                    Text("支付后自动识别，确认后发送到你的记账接收地址",
                        style = MaterialTheme.typography.bodyMedium, color = p.ink, maxLines = 2)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusTile(Icons.Filled.Notifications, "服务运行中", "等待支付通知",
                            p.green, Color(0xFFE5F5E9), Modifier.weight(1f))
                        StatusTile(Icons.Filled.ReceiptLong, "接收地址", "确认后发送",
                            p.primary, p.paperDeep, Modifier.weight(1f))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionTile(Modifier.weight(1f), p.primary, Color.White, Icons.Filled.Edit, "手动记账", onManualEntry)
                ActionTile(Modifier.weight(1f), p.secondary, p.ink, Icons.Filled.Settings, "设置", onNavigateToSettings)
            }
            Box(Modifier.fillMaxWidth().height(56.dp).clip(cardShape).background(p.card)
                .border(1.dp, p.line, cardShape).clickable { onChangeSkin() }) {
                SunnyPaperTexture()
                Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Palette, null, tint = p.primary, modifier = Modifier.size(27.dp))
                    Column(Modifier.padding(start = 10.dp)) {
                        Text("换皮肤", style = MaterialTheme.typography.titleMedium, color = p.primary, fontWeight = FontWeight.Bold)
                        Text("当前：孔版晴天", style = MaterialTheme.typography.labelMedium, color = p.muted)
                    }
                    Spacer(Modifier.weight(1f))
                    Text("›", style = MaterialTheme.typography.headlineSmall, color = p.primary)
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                FlowStep(Icons.Filled.Smartphone, "识别", "微信·支付宝通知", pink, Modifier.weight(1f))
                Text("➜", color = p.primary, style = MaterialTheme.typography.titleLarge)
                FlowStep(Icons.Filled.Search, "确认", "分类与备注", p.secondary, Modifier.weight(1f))
                Text("➜", color = p.primary, style = MaterialTheme.typography.titleLarge)
                FlowStep(Icons.Filled.Send, "发送", "接收地址处理", p.paperDeep, Modifier.weight(1f))
            }
            Box(Modifier.fillMaxWidth().clip(cardShape).background(p.card).border(1.dp, p.line, cardShape)) {
                SunnyPaperTexture()
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(46.dp).background(p.secondary.copy(alpha = .5f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Lightbulb, null, tint = p.ink, modifier = Modifier.size(30.dp))
                    }
                    Column {
                        Text("自动识别 + 手动补录", style = MaterialTheme.typography.titleMedium,
                            color = p.primary, fontWeight = FontWeight.Bold)
                        Text("让每一笔支出都轻松记录，不再遗漏", style = MaterialTheme.typography.bodyMedium, color = p.muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusTile(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String,
    subtitle: String, accent: Color, background: Color, modifier: Modifier = Modifier) {
    Row(modifier.background(background, RoundedCornerShape(11.dp)).padding(9.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(24.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall,
                color = if (accent == sunnyPalette.green) Color(0xFF087654) else sunnyPalette.primary,
                maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall,
                color = sunnyPalette.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ActionTile(modifier: Modifier, background: Color, foreground: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Box(modifier.height(65.dp).clip(cardShape).background(background).clickable { onClick() }) {
        SunnyPaperTexture()
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = foreground, modifier = Modifier.size(29.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = foreground,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 9.dp))
            Text("  ›", style = MaterialTheme.typography.titleLarge, color = foreground)
        }
    }
}

@Composable
private fun FlowStep(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String,
    subtitle: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(13.dp)).background(sunnyPalette.card)
        .border(1.dp, sunnyPalette.line, RoundedCornerShape(13.dp)).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Box(Modifier.size(43.dp).background(accent.copy(alpha = .27f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = sunnyPalette.primary, modifier = Modifier.size(27.dp))
        }
        Text(title, style = MaterialTheme.typography.titleMedium, color = sunnyPalette.primary, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = sunnyPalette.muted,
            textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
