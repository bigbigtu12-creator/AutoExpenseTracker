package com.example.autoexpense.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.autoexpense.R

/** Crops the approved permission icons from the sunny design board. */
@Composable
fun SunnyPermissionArtwork(title: String, modifier: Modifier = Modifier): Boolean {
    val center = when (title) {
        "通知权限" -> 890 to 323
        "无障碍服务" -> 890 to 440
        "通知使用权" -> 890 to 553
        "悬浮窗权限" -> 890 to 668
        "忽略电池优化" -> 890 to 785
        else -> null
    } ?: return false
    val bitmap = ImageBitmap.imageResource(R.drawable.sunny_permissions_atlas)
    Image(
        painter = BitmapPainter(
            image = bitmap,
            srcOffset = IntOffset(center.first - 36, center.second - 36),
            srcSize = IntSize(72, 72)
        ),
        contentDescription = null,
        modifier = modifier.size(49.dp)
    )
    return true
}
