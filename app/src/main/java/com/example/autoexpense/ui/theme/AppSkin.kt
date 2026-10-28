package com.example.autoexpense.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppSkin(val storageValue: String, val displayName: String) {
    Caramel("caramel", "焦糖手账"),
    Sunny("sunny", "孔版晴天"),
    Folk("folk", "花漾平涂"),
    Mono("mono", "黑白橙序");

    companion object {
        fun from(value: String): AppSkin = entries.firstOrNull { it.storageValue == value } ?: Caramel
    }
}

val LocalAppSkin = compositionLocalOf { AppSkin.Caramel }

data class SkinPalette(
    val background: Color,
    val header: Color,
    val ink: Color,
    val inkSoft: Color,
    val paper: Color,
    val paperDeep: Color,
    val card: Color,
    val line: Color,
    val muted: Color,
    val primary: Color,
    val secondary: Color,
    val green: Color,
    val blue: Color
)

private val caramelPalette = SkinPalette(
    RetroColors.Ink, RetroColors.Card, RetroColors.Ink, RetroColors.InkSoft,
    RetroColors.Paper, RetroColors.PaperDeep, RetroColors.Card, RetroColors.Line,
    RetroColors.Muted, RetroColors.Red, RetroColors.Gold, RetroColors.Green, RetroColors.Blue
)

val sunnyPalette = SkinPalette(
    Color(0xFFFFF9EF), Color(0xFF1658C8), Color(0xFF0C2F70), Color(0xFF1658C8),
    Color(0xFFFFF9EF), Color(0xFFDCEBFB), Color(0xFFFFFCF5), Color(0xFFDDD8CF),
    Color(0xFF65758C), Color(0xFF1658C8), Color(0xFFFFC743), Color(0xFF1EA983), Color(0xFF1658C8)
)

val folkPalette = SkinPalette(
    Color(0xFFFFF8F1), Color(0xFF241614), Color(0xFF241614), Color(0xFF392824),
    Color(0xFFFFF8F1), Color(0xFFFFE8E0), Color(0xFFFFFCF8), Color(0xFFEADFD8),
    Color(0xFF746A68), Color(0xFF2563D8), Color(0xFFFACC3F), Color(0xFF249A6B), Color(0xFF2563D8)
)

val monoPalette = SkinPalette(
    Color(0xFFFAF9F6), Color(0xFF111111), Color(0xFF111111), Color(0xFF303030),
    Color(0xFFFAF9F6), Color(0xFFE9E9E8), Color(0xFFFFFFFF), Color(0xFFE3E2DF),
    Color(0xFF777777), Color(0xFF111111), Color(0xFFFF5A1F), Color(0xFF1C9764), Color(0xFF111111)
)

@Composable
fun currentSkinPalette(): SkinPalette = when (LocalAppSkin.current) {
    AppSkin.Sunny -> sunnyPalette
    AppSkin.Folk -> folkPalette
    AppSkin.Mono -> monoPalette
    AppSkin.Caramel -> caramelPalette
}
