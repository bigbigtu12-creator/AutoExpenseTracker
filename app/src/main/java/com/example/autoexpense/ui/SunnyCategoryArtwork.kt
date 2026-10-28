package com.example.autoexpense.ui

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.autoexpense.R
import kotlin.math.max
import kotlin.math.min

/** Keeps the approved icon artwork while removing the design board's paper tiles. */
@Composable
fun SunnyCategoryArtwork(
    name: String,
    income: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp
): Boolean {
    val center = if (income) when (name) {
        "工资" -> 642 to 688
        "兼职" -> 750 to 688
        "投资理财" -> 862 to 684
        "人情社交" -> 963 to 688
        "报销" -> 642 to 795
        "卖二手" -> 750 to 795
        "生活费" -> 862 to 795
        "其他" -> 963 to 795
        else -> null
    } else when (name) {
        "餐饮" -> 87 to 664
        "购物" -> 189 to 664
        "交通" -> 292 to 664
        "休闲娱乐" -> 393 to 664
        "人情社交" -> 500 to 664
        "宠物" -> 87 to 765
        "养娃" -> 187 to 765
        "运动健身" -> 292 to 765
        "住房" -> 394 to 765
        "爱车" -> 497 to 765
        "医疗健康" -> 87 to 872
        "学习教育" -> 191 to 872
        "其他" -> 290 to 872
        else -> null
    }
    if (center == null) return false
    val atlas = ImageBitmap.imageResource(R.drawable.sunny_category_atlas)
    val icon = remember(atlas, center) { cutOutIcon(atlas.asAndroidBitmap(), center.first, center.second) }
    Image(bitmap = icon, contentDescription = null, modifier = modifier.size(size))
    return true
}

private fun cutOutIcon(atlas: Bitmap, centerX: Int, centerY: Int): ImageBitmap {
    // Wider than the artwork itself, but stops above the source labels and inside each tile border.
    val width = 76
    val height = 64
    val left = centerX - width / 2
    val top = centerY - height / 2
    val pixels = IntArray(width * height)
    atlas.getPixels(pixels, 0, width, left, top, width, height)
    for (index in pixels.indices) {
        val pixel = pixels[index]
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)
        val chroma = max(red, max(green, blue)) - min(red, min(green, blue))
        // The board's ivory paper has very low chroma. Keep the colored artwork and its soft edges.
        val alpha = ((chroma - 17) * 255 / 24).coerceIn(0, 255)
        pixels[index] = Color.argb(alpha, red, green, blue)
    }
    return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
}
