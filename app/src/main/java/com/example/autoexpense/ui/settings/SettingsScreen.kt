package com.example.autoexpense.ui.settings

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.R
import com.example.autoexpense.ui.sunnyPaperGrain
import com.example.autoexpense.data.local.CategoryRepository
import com.example.autoexpense.data.local.SettingsDataStore
import com.example.autoexpense.data.remote.FeishuApiService
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.currentSkinPalette
import com.example.autoexpense.ui.theme.LocalAppSkin
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.SkinPalette
import com.example.autoexpense.ui.theme.RetroPanel
import com.example.autoexpense.ui.theme.RetroPrimaryButton
import com.example.autoexpense.ui.theme.RetroSecondaryButton
import com.example.autoexpense.ui.theme.ThemeStickerIcon
import com.example.autoexpense.util.PermissionHelper
import kotlinx.coroutines.launch

private const val MODE_FEISHU_BOT = "feishu_bot"
private const val MODE_BITABLE = "bitable"
private const val MODE_BOTH = "both"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToCategoryEditor: () -> Unit,
    onSetupCompleted: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null
) {
    val palette = currentSkinPalette()
    val sunny = LocalAppSkin.current != AppSkin.Caramel
    val folk = LocalAppSkin.current == AppSkin.Folk
    val mono = LocalAppSkin.current == AppSkin.Mono
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsDataStore = remember { SettingsDataStore(context) }
    val categoryRepository = remember { CategoryRepository(context) }

    val webhookUrl by settingsDataStore.webhookUrl.collectAsState(initial = "")
    val sendMode by settingsDataStore.sendMode.collectAsState(initial = MODE_FEISHU_BOT)
    val feishuAppId by settingsDataStore.feishuAppId.collectAsState(initial = "")
    val feishuAppSecret by settingsDataStore.feishuAppSecret.collectAsState(initial = "")
    val bitableAppToken by settingsDataStore.bitableAppToken.collectAsState(initial = "")
    val bitableTableId by settingsDataStore.bitableTableId.collectAsState(initial = "")
    val categories by categoryRepository.getCategories().collectAsState(initial = emptyList())

    var modeInput by remember { mutableStateOf(MODE_FEISHU_BOT) }
    var webhookInput by remember { mutableStateOf("") }
    var appIdInput by remember { mutableStateOf("") }
    var appSecretInput by remember { mutableStateOf("") }
    var appTokenInput by remember { mutableStateOf("") }
    var tableIdInput by remember { mutableStateOf("") }
    var isNotificationListenerEnabled by remember { mutableStateOf(false) }
    var isOverlayEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isNotificationListenerEnabled = PermissionHelper.isNotificationListenerEnabled(context)
        isOverlayEnabled = PermissionHelper.canDrawOverlays(context)
    }

    LaunchedEffect(
        webhookUrl,
        sendMode,
        feishuAppId,
        feishuAppSecret,
        bitableAppToken,
        bitableTableId
    ) {
        modeInput = sendMode
        webhookInput = webhookUrl
        appIdInput = feishuAppId
        appSecretInput = feishuAppSecret
        appTokenInput = bitableAppToken
        tableIdInput = bitableTableId
    }

    val saveAction: () -> Unit = {
        if (!validateConfig(modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)) {
            Toast.makeText(context, "请补全当前模式需要的配置", Toast.LENGTH_SHORT).show()
        } else scope.launch {
            saveConfig(settingsDataStore, modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)
            Toast.makeText(context, "已保存", Toast.LENGTH_SHORT).show()
        }
    }
    val testAction: () -> Unit = {
        if (!validateConfig(modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)) {
            Toast.makeText(context, "请补全当前模式需要的配置", Toast.LENGTH_SHORT).show()
        } else scope.launch {
            saveConfig(settingsDataStore, modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)
            val result = testCurrentConfig(modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)
            Toast.makeText(context,
                result.message.ifBlank { if (result.success) "Test sent" else "Test failed" },
                Toast.LENGTH_LONG).show()
        }
    }
    val completeAction: () -> Unit = {
        if (!validateConfig(modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)) {
            Toast.makeText(context, "请补全当前模式需要的配置", Toast.LENGTH_SHORT).show()
        } else scope.launch {
            saveConfig(settingsDataStore, modeInput, webhookInput, appIdInput, appSecretInput, appTokenInput, tableIdInput)
            settingsDataStore.saveSetupCompleted(true)
            Toast.makeText(context, "配置完成", Toast.LENGTH_SHORT).show()
            onSetupCompleted()
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            if (mono) CenterAlignedTopAppBar(
                title = { Text("设置", color = palette.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onNavigateBack != null) IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = palette.ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = palette.background)
            ) else if (sunny) Box(Modifier.fillMaxWidth().height(111.dp).background(palette.paper)
                .then(if (folk) Modifier else Modifier.sunnyPaperGrain())) {
                Image(painterResource(if (folk) R.drawable.folk_header_art else R.drawable.sunny_riso_hero), null, contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.BottomEnd).size(width = 165.dp, height = 95.dp))
                Column(Modifier.align(Alignment.CenterStart).padding(start = 20.dp)) {
                    Text("设置", color = if (folk) palette.ink else palette.primary,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp), fontWeight = FontWeight.Black)
                    Box(Modifier.size(width = 42.dp, height = 5.dp).background(palette.secondary, RoundedCornerShape(8.dp)))
                    Text("配置你的自动账本", color = if (folk) palette.ink else palette.primary,
                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            } else TopAppBar(
                title = {
                    Column {
                        Text("设置", color = if (sunny) palette.header else palette.paper, style = MaterialTheme.typography.headlineMedium)
                        Text("配置你的自动账本", color = if (sunny) palette.muted else palette.paperDeep, style = MaterialTheme.typography.labelMedium)
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            RetroPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("\u53d1\u9001\u6a21\u5f0f", style = MaterialTheme.typography.titleLarge,
                        color = if (LocalAppSkin.current == AppSkin.Sunny) palette.primary else palette.ink)
                    Text(
                        text = if (sunny) "选择记账数据的发送方式" else
                            "\u5f00\u6e90\u7248\u672c\u4e0d\u4f1a\u5185\u7f6e\u4efb\u4f55\u5bc6\u94a5\u3002App ID\u3001App Secret \u548c\u8868\u683c Token \u53ea\u4fdd\u5b58\u5728\u5f53\u524d\u624b\u673a\u672c\u5730\u3002",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.muted
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModeChip(
                            text = "\u7fa4\u901a\u77e5",
                            selected = modeInput == MODE_FEISHU_BOT,
                            onClick = { modeInput = MODE_FEISHU_BOT },
                            modifier = Modifier.weight(1f)
                        )
                        ModeChip(
                            text = "\u5199\u8868\u683c",
                            selected = modeInput == MODE_BITABLE,
                            onClick = { modeInput = MODE_BITABLE },
                            modifier = Modifier.weight(1f)
                        )
                        ModeChip(
                            text = "\u5199\u8868+\u901a\u77e5",
                            selected = modeInput == MODE_BOTH,
                            onClick = { modeInput = MODE_BOTH },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (modeInput == MODE_FEISHU_BOT || modeInput == MODE_BOTH) {
                RetroPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            if (modeInput == MODE_BOTH) "\u5199\u8868\u6210\u529f\u540e\u7fa4\u901a\u77e5" else "\u98de\u4e66\u7fa4\u901a\u77e5 Webhook",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (LocalAppSkin.current == AppSkin.Sunny) palette.primary else palette.ink
                        )
                        if (modeInput == MODE_BOTH) {
                            Text(
                                "\u53ea\u6709\u591a\u7ef4\u8868\u683c\u5199\u5165\u6210\u529f\u540e\uff0c\u624d\u4f1a\u5411\u98de\u4e66\u7fa4\u53d1\u9001\u901a\u77e5\u3002",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.muted
                            )
                        }
                        SettingsTextField(
                            value = webhookInput,
                            onValueChange = { webhookInput = it },
                            label = "\u7fa4\u673a\u5668\u4eba Webhook URL"
                        )
                    }
                }
            }

            if (modeInput == MODE_BITABLE || modeInput == MODE_BOTH) {
                RetroPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("\u98de\u4e66\u591a\u7ef4\u8868\u683c\u76f4\u5199", style = MaterialTheme.typography.titleLarge,
                            color = if (LocalAppSkin.current == AppSkin.Sunny) palette.primary else palette.ink)
                        Text(
                            text = if (sunny) "填写飞书应用信息，用于自动写入多维表格。密钥只保存在当前手机。" else
                                "\u9700\u8981\u81ea\u5efa\u98de\u4e66\u5e94\u7528\uff0c\u5e76\u7ed9\u5e94\u7528\u5f00\u901a\u591a\u7ef4\u8868\u683c\u8bb0\u5f55\u5199\u5165\u6743\u9650\u3002\u4e0d\u8981\u628a\u8fd9\u4e9b\u5bc6\u94a5\u63d0\u4ea4\u5230 GitHub\u3002",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.muted
                        )
                        SettingsTextField(
                            value = appIdInput,
                            onValueChange = { appIdInput = it },
                            label = "\u98de\u4e66 App ID"
                        )
                        SettingsTextField(
                            value = appSecretInput,
                            onValueChange = { appSecretInput = it },
                            label = "\u98de\u4e66 App Secret",
                            isSecret = true
                        )
                        SettingsTextField(
                            value = appTokenInput,
                            onValueChange = { appTokenInput = it },
                            label = "\u591a\u7ef4\u8868\u683c App Token",
                            isSecret = mono
                        )
                        SettingsTextField(
                            value = tableIdInput,
                            onValueChange = { tableIdInput = it },
                            label = "\u4ea4\u6613\u6d41\u6c34 Table ID"
                        )
                        if (sunny) {
                            SettingsSaveTestRow(true, palette, saveAction, testAction)
                            RetroPrimaryButton(onClick = completeAction, modifier = Modifier.fillMaxWidth()) {
                                Text("完成配置")
                            }
                        }
                    }
                }
            }

            if (!sunny || modeInput == MODE_FEISHU_BOT) {
                RetroPanel(modifier = Modifier.fillMaxWidth()) {
                    SettingsSaveTestRow(sunny, palette, saveAction, testAction)
                }
                RetroPrimaryButton(onClick = completeAction, modifier = Modifier.fillMaxWidth()) {
                    Text("完成配置")
                }
            }

            RetroPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("\u670d\u52a1\u72b6\u6001", style = MaterialTheme.typography.titleLarge,
                        color = if (LocalAppSkin.current == AppSkin.Sunny) palette.primary else palette.ink)
                    StatusRow(
                        icon = Icons.Outlined.Notifications,
                        title = "\u901a\u77e5\u76d1\u542c",
                        enabled = isNotificationListenerEnabled,
                        accent = palette.blue,
                        onClick = {
                            (context as? Activity)?.let {
                                PermissionHelper.openNotificationListenerSettings(it)
                            }
                        }
                    )
                    StatusRow(
                        icon = Icons.Outlined.Layers,
                        title = "\u60ac\u6d6e\u7a97",
                        enabled = isOverlayEnabled,
                        accent = palette.green,
                        onClick = {
                            (context as? Activity)?.let {
                                PermissionHelper.openOverlaySettings(it)
                            }
                        }
                    )
                }
            }

            RetroPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCategoryEditor() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconBadge(Icons.Outlined.Category, palette.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("\u5206\u7c7b\u7ba1\u7406", style = MaterialTheme.typography.titleLarge,
                            color = if (LocalAppSkin.current == AppSkin.Sunny) palette.primary else palette.ink)
                        Text(
                            text = "\u5171 ${categories.size} \u4e2a\u4e00\u7ea7\u5206\u7c7b",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = palette.ink)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsSaveTestRow(
    sunny: Boolean,
    palette: SkinPalette,
    saveAction: () -> Unit,
    testAction: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (sunny) {
            Button(onClick = testAction, modifier = Modifier.weight(1f), shape = RoundedCornerShape(if (LocalAppSkin.current == AppSkin.Mono) 50.dp else 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (LocalAppSkin.current == AppSkin.Folk) Color(0xFFEDEAE7) else palette.paperDeep, contentColor = palette.ink)) {
                Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(17.dp))
                Text("测试连接")
            }
            Button(onClick = saveAction, modifier = Modifier.weight(1f), shape = RoundedCornerShape(if (LocalAppSkin.current == AppSkin.Mono) 50.dp else 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (LocalAppSkin.current == AppSkin.Mono) palette.primary else palette.secondary,
                    contentColor = if (LocalAppSkin.current == AppSkin.Mono) Color.White else palette.ink)) {
                Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(17.dp))
                Text("保存配置")
            }
        } else {
            RetroSecondaryButton(onClick = saveAction, modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(17.dp))
                Text("保存")
            }
            RetroPrimaryButton(onClick = testAction, modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(17.dp))
                Text("测试")
            }
        }
    }
}

@Composable
private fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = currentSkinPalette()
    if (LocalAppSkin.current == AppSkin.Mono) {
        Text(text, modifier = modifier
            .background(if (selected) Color(0xFFFFE4D4) else palette.paperDeep, RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(horizontal = 8.dp, vertical = 11.dp),
            color = if (selected) palette.secondary else palette.ink,
            style = MaterialTheme.typography.labelLarge, maxLines = 1,
            fontWeight = FontWeight.Bold)
        return
    }
    Text(
        text = text,
        modifier = modifier
            .background(if (selected) palette.primary else if (LocalAppSkin.current == AppSkin.Folk) Color(0xFFEDEAE7) else palette.paperDeep, RoundedCornerShape(12.dp))
            .border(if (selected) 2.dp else 1.dp,
                if (selected) { if (LocalAppSkin.current == AppSkin.Folk) palette.primary else palette.secondary }
                else palette.line.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 9.dp),
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) Color.White else palette.ink,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isSecret: Boolean = false
) {
    val palette = currentSkinPalette()
    if (LocalAppSkin.current != AppSkin.Caramel) {
        val entry: @Composable (Modifier) -> Unit = { entryModifier ->
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = entryModifier.height(43.dp)
                    .background(if (LocalAppSkin.current == AppSkin.Mono) palette.paperDeep else palette.paper, RoundedCornerShape(9.dp))
                    .border(if (LocalAppSkin.current == AppSkin.Mono) 0.dp else 1.dp, palette.line, RoundedCornerShape(9.dp)),
                singleLine = true,
                visualTransformation = if (isSecret) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.ink),
                cursorBrush = SolidColor(palette.primary),
                decorationBox = { inner ->
                    Box(Modifier.fillMaxSize().padding(horizontal = 10.dp), contentAlignment = Alignment.CenterStart) {
                        inner()
                    }
                }
            )
        }
        if (label.contains("Webhook")) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = palette.muted)
                entry(Modifier.fillMaxWidth())
            }
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(label.replace("飞书 ", ""), modifier = Modifier.width(92.dp),
                    style = MaterialTheme.typography.labelMedium, color = palette.muted,
                    maxLines = 2)
                entry(Modifier.weight(1f))
            }
        }
        return
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isSecret) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.line.copy(alpha = 0.65f),
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

@Composable
private fun StatusRow(
    icon: ImageVector,
    title: String,
    enabled: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val palette = currentSkinPalette()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.paperDeep.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .border(1.dp, palette.line.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .clickable(enabled = !enabled) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconBadge(icon, accent)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.ink)
            Text(
                text = if (enabled) "\u5df2\u5f00\u542f" else "\u672a\u5f00\u542f\uff0c\u70b9\u51fb\u5904\u7406",
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled) palette.green else palette.primary,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = if (enabled) palette.green else palette.muted
        )
    }
}

@Composable
private fun IconBadge(icon: ImageVector, color: Color) {
    ThemeStickerIcon(icon = icon, accent = color, size = 42.dp)
}

private fun validateConfig(
    mode: String,
    webhook: String,
    appId: String,
    appSecret: String,
    appToken: String,
    tableId: String
): Boolean {
    val needsWebhook = mode == MODE_FEISHU_BOT || mode == MODE_BOTH
    val needsBitable = mode == MODE_BITABLE || mode == MODE_BOTH
    if (needsWebhook && webhook.trim().isBlank()) return false
    if (needsBitable) {
        return appId.trim().isNotBlank() &&
                appSecret.trim().isNotBlank() &&
                appToken.trim().isNotBlank() &&
                tableId.trim().isNotBlank()
    }
    return true
}

private suspend fun saveConfig(
    settings: SettingsDataStore,
    mode: String,
    webhook: String,
    appId: String,
    appSecret: String,
    appToken: String,
    tableId: String
) {
    settings.saveSendMode(mode)
    settings.saveWebhookUrl(webhook.trim())
    settings.saveFeishuBitableConfig(
        appId = appId.trim(),
        appSecret = appSecret.trim(),
        appToken = appToken.trim(),
        tableId = tableId.trim(),
        notificationWebhook = ""
    )
}

private suspend fun testCurrentConfig(
    mode: String,
    webhook: String,
    appId: String,
    appSecret: String,
    appToken: String,
    tableId: String
): FeishuApiService.SendResult {
    return when (mode) {
        MODE_BITABLE, MODE_BOTH -> {
            val bitableResult = FeishuApiService.writeExpenseToBitableDetailed(
                config = FeishuApiService.BitableConfig(
                    appId = appId.trim(),
                    appSecret = appSecret.trim(),
                    appToken = appToken.trim(),
                    tableId = tableId.trim()
                ),
                amount = 0.01,
                paymentMethod = "Test",
                category = "Test",
                subCategory = "Test",
                merchant = "Settings test",
                rawText = "Settings test"
            )
            if (!bitableResult.success) {
                return bitableResult
            }
            if (mode == MODE_BOTH) {
                val notifyWebhook = webhook.trim()
                if (notifyWebhook.isNotBlank()) {
                    val notifyResult = FeishuApiService.sendExpenseDetailed(
                        webhookUrl = notifyWebhook,
                        amount = 0.01,
                        paymentMethod = "Test",
                        category = "Test",
                        subCategory = "Test",
                        merchant = "Settings test"
                    )
                    if (!notifyResult.success) {
                        return notifyResult
                    }
                }
            }
            if (mode == MODE_BITABLE) bitableResult else FeishuApiService.SendResult(true, bitableResult.message)
        }

        else -> FeishuApiService.sendExpenseDetailed(
            webhookUrl = webhook.trim(),
            amount = 0.01,
            paymentMethod = "Test",
            category = "Test",
            subCategory = "Test",
            merchant = "Settings test"
        )
    }
}


