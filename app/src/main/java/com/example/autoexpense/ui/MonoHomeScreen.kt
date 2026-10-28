package com.example.autoexpense.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.ui.theme.monoPalette

private val monoOrange = Color(0xFFFF5A1F)
private val monoBlack = Color(0xFF111111)

@Composable
fun MonoHomeScreen(
    onNavigateToSettings: () -> Unit,
    onManualEntry: () -> Unit,
    onChangeSkin: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(monoPalette.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(218.dp)) {
            Text(
                "自动\n记账", color = monoBlack, fontWeight = FontWeight.Black,
                fontSize = 86.sp, lineHeight = 84.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Row(Modifier.align(Alignment.TopEnd), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                MonoHeaderAction(Icons.Filled.Palette, "换皮肤", onChangeSkin)
                MonoHeaderAction(Icons.Filled.Settings, "设置", onNavigateToSettings)
            }
        }

        Column(
            Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(19.dp))
                .border(1.dp, monoPalette.line, RoundedCornerShape(19.dp))
                .padding(horizontal = 17.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(15.dp).background(monoOrange, CircleShape))
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("账本正在后台守候", color = monoBlack, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("服务运行中 · 等待支付通知", color = monoPalette.muted, fontSize = 14.sp)
                }
                Icon(Icons.Filled.ChevronRight, null, tint = monoPalette.muted, modifier = Modifier.size(22.dp))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(monoPalette.line))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("◎", color = monoBlack, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("接收地址", color = monoBlack, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 10.dp))
                Spacer(Modifier.weight(1f))
                Text("确认后发送", color = monoPalette.muted, fontSize = 13.sp)
                Icon(Icons.Filled.ChevronRight, null, tint = monoPalette.muted, modifier = Modifier.size(19.dp))
            }
        }

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("记账流程", color = monoBlack, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                MonoFlowStep("1", "识别", "支付通知", Modifier.weight(1f))
                MonoFlowStep("2", "确认", "分类与金额", Modifier.weight(1f))
                MonoFlowStep("3", "发送", "记入账本", Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(90.dp))

        Row(
            Modifier.fillMaxWidth().height(58.dp)
                .background(monoBlack, RoundedCornerShape(50.dp))
                .clickable { onManualEntry() }.padding(horizontal = 19.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(25.dp))
            Text("手动记账", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 13.dp).weight(1f))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White)
        }
        Text("自动识别 + 手动补录 · 每一笔都能及时记录", color = monoPalette.muted,
            fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun MonoHeaderAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(Modifier.width(48.dp).clickable { onClick() }, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(39.dp).background(monoPalette.paperDeep, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = monoBlack, modifier = Modifier.size(21.dp))
        }
        Text(label, color = monoBlack, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun MonoFlowStep(number: String, title: String, subtitle: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(40.dp).background(Color(0xFFFFE4D4), CircleShape), contentAlignment = Alignment.Center) {
            Text(number, color = monoOrange, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
        Text(title, color = monoBlack, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = monoPalette.muted, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}
