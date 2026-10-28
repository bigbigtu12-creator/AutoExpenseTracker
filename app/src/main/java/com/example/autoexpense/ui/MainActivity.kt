package com.example.autoexpense.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import com.example.autoexpense.data.local.SettingsDataStore
import com.example.autoexpense.service.KeepAliveService
import com.example.autoexpense.ui.settings.CategoryEditorScreen
import com.example.autoexpense.ui.settings.SettingsScreen
import com.example.autoexpense.ui.theme.AutoExpenseRetroTheme
import com.example.autoexpense.ui.theme.ReadableRetroTheme
import com.example.autoexpense.ui.theme.AppSkin
import com.example.autoexpense.ui.theme.LocalAppSkin
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.autoexpense.util.PermissionHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(54, 34, 23)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

        startKeepAliveService()

        val showExpense = intent?.getBooleanExtra("showExpense", false) ?: false
        val expenseAmount = intent?.getDoubleExtra("amount", 0.0) ?: 0.0
        val expensePaymentMethod = intent?.getStringExtra("paymentMethod") ?: ""
        val expenseRawText = intent?.getStringExtra("rawText") ?: ""

        setContent {
            val settingsDataStore = remember { SettingsDataStore(this) }
            val selectedSkinValue by settingsDataStore.appSkin.collectAsState(initial = "caramel")
            val selectedSkin = AppSkin.from(selectedSkinValue)
            val scope = rememberCoroutineScope()
            var showPermissions by remember { mutableStateOf(!areAllPermissionsGranted()) }
            var currentScreen by remember {
                mutableStateOf(if (showExpense) Screen.EXPENSE_CONFIRM else Screen.HOME)
            }
            val isSkinPicker = currentScreen == Screen.SKIN_PICKER
            val chromeSkin = if (isSkinPicker) AppSkin.Caramel else selectedSkin
            SideEffect {
                window.statusBarColor = if (isSkinPicker) android.graphics.Color.rgb(38, 23, 15)
                    else if (chromeSkin == AppSkin.Mono) android.graphics.Color.rgb(250, 249, 246)
                    else if (chromeSkin == AppSkin.Sunny) android.graphics.Color.rgb(255, 249, 239)
                    else if (chromeSkin == AppSkin.Folk) android.graphics.Color.rgb(255, 248, 241)
                    else android.graphics.Color.rgb(54, 34, 23)
                window.navigationBarColor = if (isSkinPicker) android.graphics.Color.rgb(38, 23, 15)
                    else if (chromeSkin == AppSkin.Mono) android.graphics.Color.rgb(250, 249, 246)
                    else if (chromeSkin == AppSkin.Folk) android.graphics.Color.rgb(255, 248, 241)
                    else if (chromeSkin == AppSkin.Sunny) android.graphics.Color.rgb(255, 249, 239)
                    else android.graphics.Color.rgb(54, 34, 23)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                if (chromeSkin != AppSkin.Caramel) {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                        (android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR).inv()
                }
            }
            CompositionLocalProvider(LocalAppSkin provides selectedSkin) {
            AutoExpenseRetroTheme {
                val webhookUrl by settingsDataStore.webhookUrl.collectAsState(initial = "")
                val isSetupCompleted by settingsDataStore.isSetupCompleted.collectAsState(initial = false)
                val sendMode by settingsDataStore.sendMode.collectAsState(initial = "feishu_bot")
                val feishuAppId by settingsDataStore.feishuAppId.collectAsState(initial = "")
                val feishuAppSecret by settingsDataStore.feishuAppSecret.collectAsState(initial = "")
                val bitableAppToken by settingsDataStore.bitableAppToken.collectAsState(initial = "")
                val bitableTableId by settingsDataStore.bitableTableId.collectAsState(initial = "")
                val isDeliveryConfigured = when (sendMode) {
                    "bitable" -> feishuAppId.isNotBlank() &&
                            feishuAppSecret.isNotBlank() &&
                            bitableAppToken.isNotBlank() &&
                            bitableTableId.isNotBlank()
                    "both" -> webhookUrl.isNotBlank() &&
                            feishuAppId.isNotBlank() &&
                            feishuAppSecret.isNotBlank() &&
                            bitableAppToken.isNotBlank() &&
                            bitableTableId.isNotBlank()
                    else -> webhookUrl.isNotBlank()
                }

                if (showExpense) {
                    ExpenseConfirmScreen(
                        amount = expenseAmount,
                        paymentMethod = expensePaymentMethod,
                        rawText = expenseRawText,
                        onBack = { finish() }
                    )
                    return@AutoExpenseRetroTheme
                }

                if (showPermissions) {
                    ReadableRetroTheme {
                        PermissionScreen(
                            onAllPermissionsGranted = {
                                showPermissions = false
                                currentScreen = Screen.SETTINGS
                                startKeepAliveService()
                            }
                        )
                    }
                    return@AutoExpenseRetroTheme
                }

                when (currentScreen) {
                    Screen.HOME -> {
                        if (!isSetupCompleted || !isDeliveryConfigured) {
                            ReadableRetroTheme { SettingsScreen(
                                onNavigateToCategoryEditor = {
                                    currentScreen = Screen.CATEGORY_EDITOR
                                },
                                onSetupCompleted = {
                                    currentScreen = Screen.HOME
                                }
                            ) }
                        } else {
                            val home: @Composable () -> Unit = {
                                if (selectedSkin == AppSkin.Mono) MonoHomeScreen(
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                    onManualEntry = { currentScreen = Screen.EXPENSE_CONFIRM },
                                    onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                                ) else if (selectedSkin == AppSkin.Folk) FolkHomeScreen(
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                    onManualEntry = { currentScreen = Screen.EXPENSE_CONFIRM },
                                    onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                                ) else if (selectedSkin == AppSkin.Sunny) SunnyHomeScreen(
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                    onManualEntry = { currentScreen = Screen.EXPENSE_CONFIRM },
                                    onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                                ) else HomeScreen(
                                onNavigateToSettings = {
                                    currentScreen = Screen.SETTINGS
                                },
                                onManualEntry = {
                                    currentScreen = Screen.EXPENSE_CONFIRM
                                },
                                onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                                )
                            }
                            home()
                        }
                    }

                    Screen.SETTINGS -> {
                        ReadableRetroTheme { SettingsScreen(
                            onNavigateToCategoryEditor = {
                                currentScreen = Screen.CATEGORY_EDITOR
                            },
                            onSetupCompleted = {
                                currentScreen = Screen.HOME
                            },
                            onNavigateBack = { currentScreen = Screen.HOME }
                        ) }
                    }

                    Screen.CATEGORY_EDITOR -> {
                        CategoryEditorScreen(
                            onNavigateBack = {
                                currentScreen = Screen.SETTINGS
                            }
                        )
                    }

                    Screen.SKIN_PICKER -> {
                        SkinPickerScreen(
                            selectedSkin = selectedSkin,
                            onSelect = { next -> scope.launch {
                                settingsDataStore.saveAppSkin(next.storageValue)
                            } },
                            onBack = { currentScreen = Screen.HOME }
                        )
                    }

                    Screen.EXPENSE_CONFIRM -> {
                        Box {
                            if (selectedSkin == AppSkin.Mono) MonoHomeScreen(
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                onManualEntry = {},
                                onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                            ) else if (selectedSkin == AppSkin.Folk) FolkHomeScreen(
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                onManualEntry = {},
                                onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                            ) else if (selectedSkin == AppSkin.Sunny) SunnyHomeScreen(
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                onManualEntry = {},
                                onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                            ) else HomeScreen(
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                onManualEntry = {},
                                onChangeSkin = { currentScreen = Screen.SKIN_PICKER }
                            )
                            ExpenseConfirmDialog(
                                amount = 0.0,
                                paymentMethod = "手动记账",
                                rawText = "",
                                transactionType = "expense",
                                onDismiss = { currentScreen = Screen.HOME }
                            )
                        }
                    }
                }
            }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun startKeepAliveService() {
        try {
            val intent = Intent(this, KeepAliveService::class.java)
            startForegroundService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return PermissionHelper.hasNotificationPermission(this) &&
                PermissionHelper.isAccessibilityServiceEnabled(this) &&
                PermissionHelper.isNotificationListenerEnabled(this) &&
                PermissionHelper.canDrawOverlays(this) &&
                PermissionHelper.isIgnoringBatteryOptimizations(this)
    }

    private enum class Screen {
        HOME,
        SETTINGS,
        CATEGORY_EDITOR,
        SKIN_PICKER,
        EXPENSE_CONFIRM
    }
}

