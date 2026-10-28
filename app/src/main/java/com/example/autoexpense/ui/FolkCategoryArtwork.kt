package com.example.autoexpense.ui

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.autoexpense.R
import androidx.compose.ui.graphics.ImageBitmap
import kotlin.math.max
import kotlin.math.min

/** Transparent, consistently sized icons isolated from the approved artboard. */
@Composable
fun FolkCategoryArtwork(name: String, income: Boolean, modifier: Modifier = Modifier, size: Dp = 45.dp): Boolean {
    val center = if (income) when (name) {
        "工资" -> 376 to 723
        "兼职" -> 637 to 723
        "投资理财" -> 894 to 723
        "人情社交" -> 1154 to 723
        "报销" -> 376 to 870
        "卖二手" -> 637 to 870
        "生活费" -> 894 to 870
        "其他" -> 1154 to 870
        else -> null
    } else when (name) {
        "餐饮" -> 300 to 159
        "休闲娱乐" -> 520 to 159
        "购物" -> 765 to 159
        "交通" -> 1000 to 159
        "人情社交" -> 1220 to 159
        "宠物" -> 300 to 326
        "养娃" -> 520 to 326
        "运动健身" -> 765 to 326
        "住房" -> 1000 to 326
        "爱车" -> 1220 to 326
        "医疗健康" -> 300 to 485
        "学习教育" -> 520 to 485
        "其他" -> 765 to 495
        else -> null
    }
    if (center == null) return false
    val atlas = ImageBitmap.imageResource(R.drawable.folk_category_atlas)
    val icon = remember(atlas, center) { isolateFolkIcon(atlas.asAndroidBitmap(), center.first, center.second) }
    Image(bitmap = icon, contentDescription = null, modifier = modifier.size(size))
    return true
}

private fun isolateFolkIcon(atlas: Bitmap, cx: Int, cy: Int): ImageBitmap {
    val width = 170
    val height = if (cx == 765 && cy == 495) 72 else 108
    val pixels = IntArray(width * height)
    atlas.getPixels(pixels, 0, width, cx - width / 2, cy - height / 2, width, height)
    for (i in pixels.indices) {
        val pixel = pixels[i]
        val r = AndroidColor.red(pixel)
        val g = AndroidColor.green(pixel)
        val b = AndroidColor.blue(pixel)
        val chroma = max(r, max(g, b)) - min(r, min(g, b))
        val dark = 255 - min(r, min(g, b))
        val alpha = max(((chroma - 17) * 255 / 38).coerceIn(0, 255),
            ((dark - 105) * 255 / 60).coerceIn(0, 255))
        pixels[i] = AndroidColor.argb(alpha, r, g, b)
    }
    // Generated board ornaments occasionally touch a crop by just a few pixels.
    // Remove only tiny isolated islands; the smallest intentional dots remain larger.
    val visited = BooleanArray(pixels.size)
    val queue = IntArray(pixels.size)
    for (start in pixels.indices) {
        if (visited[start] || AndroidColor.alpha(pixels[start]) < 80) continue
        var head = 0
        var tail = 0
        visited[start] = true
        queue[tail++] = start
        while (head < tail) {
            val at = queue[head++]
            val x = at % width
            val y = at / width
            for (dy in -1..1) for (dx in -1..1) {
                val nx = x + dx
                val ny = y + dy
                if (nx !in 0 until width || ny !in 0 until height) continue
                val next = ny * width + nx
                if (!visited[next] && AndroidColor.alpha(pixels[next]) >= 80) {
                    visited[next] = true
                    queue[tail++] = next
                }
            }
        }
        if (tail < 24) for (j in 0 until tail) pixels[queue[j]] = AndroidColor.TRANSPARENT
    }
    return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
}
