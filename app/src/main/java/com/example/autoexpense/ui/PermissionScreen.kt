package com.example.autoexpense.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.autoexpense.R
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.LocalAppSkin
import com.example.autoexpense.ui.sunnyPaperGrain
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.currentSkinPalette
import com.example.autoexpense.ui.theme.RetroPanel
import com.example.autoexpense.ui.theme.RetroPrimaryButton
import com.example.autoexpense.ui.theme.RetroSecondaryButton
import com.example.autoexpense.ui.theme.ThemeStickerIcon
import com.example.autoexpense.util.PermissionHelper
import kotlinx.coroutines.delay

@Composable
fun PermissionScreen(
    onAllPermissionsGranted: () -> Unit
) {
    val palette = currentSkinPalette()
    val sunny = LocalAppSkin.current != AppSkin.Caramel
    val folk = LocalAppSkin.current == AppSkin.Folk
    val mono = LocalAppSkin.current == AppSkin.Mono
    val context = LocalContext.current
    val activity = context as Activity

    var notificationListenerEnabled by remember { mutableStateOf(PermissionHelper.isNotificationListenerEnabled(context)) }
    var notificationPermEnabled by remember { mutableStateOf(PermissionHelper.hasNotificationPermission(context)) }
    var overlayEnabled by remember { mutableStateOf(PermissionHelper.canDrawOverlays(context)) }
    var batteryOptimized by remember { mutableStateOf(PermissionHelper.isIgnoringBatteryOptimizations(context)) }
    var accessibilityEnabled by remember { mutableStateOf(PermissionHelper.isAccessibilityServiceEnabled(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            notificationListenerEnabled = PermissionHelper.isNotificationListenerEnabled(context)
            notificationPermEnabled = PermissionHelper.hasNotificationPermission(context)
            overlayEnabled = PermissionHelper.canDrawOverlays(context)
            batteryOptimized = PermissionHelper.isIgnoringBatteryOptimizations(context)
            accessibilityEnabled = PermissionHelper.isAccessibilityServiceEnabled(context)
        }
    }

    val allGranted = notificationPermEnabled &&
        notificationListenerEnabled &&
        overlayEnabled &&
        batteryOptimized &&
        accessibilityEnabled

    Box(Modifier.fillMaxSize().background(palette.background)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .then(if (sunny && !folk && !mono) Modifier.sunnyPaperGrain() else Modifier)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (mono) {
            Text("开通后台记账", color = palette.ink, fontSize = 36.sp, fontWeight = FontWeight.Black)
        } else if (sunny) {
            Box(Modifier.fillMaxWidth().height(86.dp)) {
                Image(painterResource(if (folk) R.drawable.folk_header_art else R.drawable.sunny_riso_hero), null, contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.BottomEnd).size(width = 145.dp, height = 80.dp))
                Text("开通后台记账", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 31.sp),
                    color = if (folk) palette.ink else palette.primary, fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.CenterStart))
                if (folk) Text("✹", color = Color(0xFFF5544D), fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.TopStart))
            }
        } else Text("开通后台记账", style = MaterialTheme.typography.headlineMedium, color = palette.header)
        Text(
            text = "这些权限只用于识别支付通知、弹出确认单和保持服务稳定。",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.muted
        )

        val permissionRows: @Composable () -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(if (sunny) 11.dp else 10.dp)) {
                PermissionCard(
                    icon = Icons.Outlined.Notifications,
                    title = "通知权限",
                    description = "允许发送记账提醒与确认通知",
                    granted = notificationPermEnabled,
                    accent = palette.secondary,
                    onGrantClick = { PermissionHelper.requestNotificationPermission(activity) }
                )
                PermissionCard(
                    icon = Icons.Outlined.RecordVoiceOver,
                    title = "无障碍服务",
                    description = "识别微信、支付宝支付提示",
                    granted = accessibilityEnabled,
                    accent = palette.primary,
                    onGrantClick = { PermissionHelper.openAccessibilitySettings(activity) }
                )
                PermissionCard(
                    icon = Icons.Outlined.Notifications,
                    title = "通知使用权",
                    description = "备用监听通道，提升识别成功率",
                    granted = notificationListenerEnabled,
                    accent = palette.blue,
                    onGrantClick = { PermissionHelper.openNotificationListenerSettings(activity) }
                )
                PermissionCard(
                    icon = Icons.Outlined.Layers,
                    title = "悬浮窗权限",
                    description = "支付后弹出轻量确认单",
                    granted = overlayEnabled,
                    accent = palette.green,
                    onGrantClick = { PermissionHelper.openOverlaySettings(activity) }
                )
                PermissionCard(
                    icon = Icons.Outlined.BatterySaver,
                    title = "忽略电池优化",
                    description = "减少后台服务被系统关闭",
                    granted = batteryOptimized,
                    accent = palette.secondary,
                    onGrantClick = { PermissionHelper.requestIgnoreBatteryOptimizations(activity) }
                )
            }
        }
        if (sunny) permissionRows() else RetroPanel(modifier = Modifier.fillMaxWidth()) { permissionRows() }

        if (allGranted) {
            RetroPrimaryButton(
                onClick = onAllPermissionsGranted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("进入应用")
            }
        } else {
            Text(
                text = "完成上面的开关后，这里会出现进入应用按钮。",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.muted
            )
        }
    }
    if (folk) Image(painterResource(R.drawable.folk_bottom_art), contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth().height(75.dp).align(Alignment.BottomCenter))
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
    accent: Color,
    onGrantClick: () -> Unit
) {
    val palette = currentSkinPalette()
    val sunny = LocalAppSkin.current != AppSkin.Caramel
    val folk = LocalAppSkin.current == AppSkin.Folk
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (folk) Modifier.heightIn(min = 99.dp) else Modifier)
            .background(if (LocalAppSkin.current == AppSkin.Mono) palette.paperDeep else if (sunny) palette.card else palette.paperDeep.copy(alpha = 0.7f), RoundedCornerShape(13.dp))
            .border(if (LocalAppSkin.current == AppSkin.Mono) 0.dp else 1.dp, palette.line.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(if (sunny) 14.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (folk) {
            Box(Modifier.size(49.dp).background(accent.copy(alpha = .14f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(30.dp))
            }
        } else if (LocalAppSkin.current == AppSkin.Mono || !sunny || !SunnyPermissionArtwork(title)) {
            ThemeStickerIcon(icon = icon, accent = accent, size = 49.dp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = palette.ink)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = palette.muted)
        }
        if (granted) {
            if (sunny) Text("✓ 已开启  ›", style = MaterialTheme.typography.labelLarge,
                color = palette.green, fontWeight = FontWeight.Bold,
                modifier = Modifier.background(palette.green.copy(alpha = .09f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp))
            else ThemeStickerIcon(icon = Icons.Outlined.CheckCircle, accent = palette.green, size = 30.dp)
        } else {
            RetroSecondaryButton(onClick = onGrantClick) {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Text("开启")
            }
        }
    }
}

