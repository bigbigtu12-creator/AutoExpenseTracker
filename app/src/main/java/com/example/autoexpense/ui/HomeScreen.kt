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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.R
import com.example.autoexpense.ui.theme.RetroColors
import com.example.autoexpense.ui.theme.RetroPrimaryButton
import com.example.autoexpense.ui.theme.TornPaperPanel

private val DieCutShape = GenericShape { size, _ ->
    val w = size.width; val h = size.height
    moveTo(w * .18f, 0f); lineTo(w * .40f, h * .04f); lineTo(w * .62f, 0f)
    lineTo(w * .82f, h * .06f); lineTo(w * .96f, h * .20f); lineTo(w, h * .42f)
    lineTo(w * .96f, h * .65f); lineTo(w, h * .82f); lineTo(w * .82f, h * .98f)
    lineTo(w * .58f, h); lineTo(w * .35f, h * .96f); lineTo(w * .17f, h)
    lineTo(0f, h * .80f); lineTo(w * .04f, h * .55f); lineTo(0f, h * .31f)
    close()
}

@Composable
fun HomeScreen(onNavigateToSettings: () -> Unit, onManualEntry: () -> Unit, onChangeSkin: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF362217), RetroColors.Ink)))) {
      BoxWithConstraints(Modifier.fillMaxSize()) {
        val heroHeight = (maxHeight * 0.31f).coerceIn(255.dp, 295.dp)
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(102.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("自动记账", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 35.sp), color = RetroColors.Paper, fontWeight = FontWeight.Black)
                    Text("账本正在后台守候", style = MaterialTheme.typography.bodyMedium, color = RetroColors.PaperDeep)
                    Box(Modifier.padding(top = 4.dp).size(width = 88.dp, height = 2.dp)
                        .background(RetroColors.Gold, RoundedCornerShape(50)))
                }
                Box(
                    modifier = Modifier.size(43.dp).border(1.5.dp, RetroColors.Paper, CircleShape)
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Settings, contentDescription = "设置", tint = RetroColors.Paper)
                }
            }

            Box(Modifier.fillMaxWidth().height(heroHeight)) {
                TornPaperPanel(Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(start = 17.dp, end = 17.dp, top = 27.dp, bottom = 11.dp)
                    ) {
                        Text("自动记账", style = MaterialTheme.typography.headlineMedium.copy(fontSize = 30.sp),
                            color = RetroColors.Ink, fontWeight = FontWeight.Black)
                        Text("支付后自动识别，\n确认后发送到你的记账接收地址",
                            style = MaterialTheme.typography.bodyMedium, color = RetroColors.InkSoft,
                            modifier = Modifier.padding(top = 7.dp))
                        Spacer(Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            StatusTile(0, "服务运行中", "等待支付通知",
                                RetroColors.Green, Modifier.weight(1f))
                            StatusTile(1, "接收地址", "确认后发送",
                                RetroColors.Red, Modifier.weight(1f))
                        }
                    }
                }
                val context = LocalContext.current
                val toffee = remember {
                    tintToffeeBorder(android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.toffee_detective))
                }
                Image(
                    bitmap = toffee.asImageBitmap(),
                    contentDescription = "Toffee 正在查看账单",
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (-2).dp, y = (-78).dp)
                        .size(width = 205.dp, height = 240.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RetroPrimaryButton(onClick = onManualEntry, modifier = Modifier.weight(1f).height(58.dp)) {
                    HomeSticker(2, 45)
                    Text("手动记账", style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp))
                }
                Box(
                    modifier = Modifier.weight(1f).height(58.dp)
                        .background(RetroColors.InkSoft, RoundedCornerShape(12.dp))
                        .border(1.dp, RetroColors.Line.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HomeSticker(3, 45)
                        Text("设置", style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp), color = RetroColors.Paper)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(45.dp)
                    .background(RetroColors.InkSoft.copy(alpha = 0.72f), RoundedCornerShape(11.dp))
                    .border(1.dp, RetroColors.Line.copy(alpha = 0.65f), RoundedCornerShape(11.dp))
                    .clickable { onChangeSkin() }
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.Palette, contentDescription = null, tint = RetroColors.Gold, modifier = Modifier.size(24.dp))
                Text("换皮肤", style = MaterialTheme.typography.titleMedium, color = RetroColors.Paper)
                Spacer(Modifier.weight(1f))
                Text("焦糖手账", style = MaterialTheme.typography.bodyMedium, color = RetroColors.PaperDeep)
                Text("›", style = MaterialTheme.typography.titleLarge, color = RetroColors.PaperDeep)
            }

            TornPaperPanel(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 9.dp, vertical = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlowTile(4, "识别", "微信·支付宝通知", Modifier.weight(1f))
                    Text("➜", color = RetroColors.Red, style = MaterialTheme.typography.titleLarge)
                    FlowTile(5, "确认", "分类与备注", Modifier.weight(1f))
                    Text("➜", color = RetroColors.Red, style = MaterialTheme.typography.titleLarge)
                    FlowTile(6, "发送", "接收地址处理", Modifier.weight(1f))
                }
            }

            TornPaperPanel(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HomeSticker(7, 60)
                    Column {
                        Text("自动识别 + 手动补录", style = MaterialTheme.typography.titleMedium, color = RetroColors.Ink)
                        Text("自动获取支付通知，确认后发送到你的记账接收地址；也可以随时手动记录一笔。",
                            style = MaterialTheme.typography.bodyMedium, color = RetroColors.Muted)
                    }
                }
            }
        }
      }
    }
}

@Composable
private fun StatusTile(sticker: Int, title: String, subtitle: String, accent: Color, modifier: Modifier) {
    Row(
        modifier = modifier.background(accent.copy(alpha = 0.12f), RoundedCornerShape(11.dp))
            .padding(horizontal = 8.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        HomeSticker(sticker, 53)
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp), color = RetroColors.Ink, maxLines = 1)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), color = RetroColors.Muted, maxLines = 1)
        }
    }
}

@Composable
private fun FlowTile(sticker: Int, title: String, subtitle: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        HomeSticker(sticker, 68)
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp), color = RetroColors.Ink)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = RetroColors.Muted,
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun HomeSticker(index: Int, size: Int) {
    val context = LocalContext.current
    val source = remember { android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.home_sticker_source) }
    val painter = remember(source, index) {
        val crops = arrayOf(
            intArrayOf(256, 405, 75, 88), intArrayOf(474, 407, 79, 82),
            intArrayOf(253, 536, 80, 76), intArrayOf(484, 539, 81, 81),
            intArrayOf(251, 647, 116, 106), intArrayOf(419, 645, 111, 106),
            intArrayOf(570, 646, 111, 106), intArrayOf(252, 834, 82, 90)
        )
        val rect = crops[index]
        val crop = android.graphics.Bitmap.createBitmap(source, rect[0], rect[1], rect[2], rect[3])
        val pixels = IntArray(crop.width * crop.height)
        crop.getPixels(pixels, 0, crop.width, 0, 0, crop.width, crop.height)
        val background = pixels[0]
        val queue = IntArray(pixels.size)
        val visited = BooleanArray(pixels.size)
        var head = 0; var tail = 0
        fun enqueue(p: Int) {
            if (visited[p]) return
            val pixel = pixels[p]
            val dr = android.graphics.Color.red(pixel) - android.graphics.Color.red(background)
            val dg = android.graphics.Color.green(pixel) - android.graphics.Color.green(background)
            val db = android.graphics.Color.blue(pixel) - android.graphics.Color.blue(background)
            if (dr * dr + dg * dg + db * db < 27 * 27) { visited[p] = true; queue[tail++] = p }
        }
        for (x in 0 until crop.width) { enqueue(x); enqueue((crop.height - 1) * crop.width + x) }
        for (y in 0 until crop.height) { enqueue(y * crop.width); enqueue(y * crop.width + crop.width - 1) }
        while (head < tail) {
            val p = queue[head++]; val x = p % crop.width; val y = p / crop.width
            pixels[p] = android.graphics.Color.TRANSPARENT
            if (x > 0) enqueue(p - 1)
            if (x < crop.width - 1) enqueue(p + 1)
            if (y > 0) enqueue(p - crop.width)
            if (y < crop.height - 1) enqueue(p + crop.width)
        }
        if (index == 2 || index == 3) {
            // Retain the cream paper sticker and its nearby ink, discarding the mockup's
            // dark rectangular button photographed underneath it.
            val pale = BooleanArray(pixels.size) { p ->
                val c = pixels[p]
                android.graphics.Color.alpha(c) > 0 &&
                    android.graphics.Color.red(c) > 160 &&
                    android.graphics.Color.green(c) > 130 &&
                    android.graphics.Color.blue(c) > 90
            }
            for (y in 0 until crop.height) for (x in 0 until crop.width) {
                val p = y * crop.width + x
                if (android.graphics.Color.alpha(pixels[p]) == 0) continue
                var nearPaper = false
                for (dy in -5..5) for (dx in -5..5) {
                    val xx = x + dx; val yy = y + dy
                    if (xx in 0 until crop.width && yy in 0 until crop.height &&
                        pale[yy * crop.width + xx] && dx * dx + dy * dy <= 25) nearPaper = true
                }
                if (!nearPaper) pixels[p] = android.graphics.Color.TRANSPARENT
            }
        }
        if (index == 0 || index == 1) {
            val cx = crop.width * .5f
            val cy = crop.height * .5f
            val rx = crop.width * .46f
            val ry = crop.height * .41f
            for (y in 0 until crop.height) for (x in 0 until crop.width) {
                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                if (dx * dx + dy * dy > 1f) pixels[y * crop.width + x] = android.graphics.Color.TRANSPARENT
            }
        }
        BitmapPainter(android.graphics.Bitmap.createBitmap(pixels, crop.width, crop.height,
            android.graphics.Bitmap.Config.ARGB_8888).asImageBitmap())
    }
    Image(painter = painter, contentDescription = null, modifier = Modifier.size(size.dp))
}

private fun tintToffeeBorder(source: android.graphics.Bitmap): android.graphics.Bitmap {
    val width = source.width
    val height = source.height
    val pixels = IntArray(width * height)
    source.getPixels(pixels, 0, width, 0, 0, width, height)
    fun isPale(pixel: Int): Boolean = android.graphics.Color.alpha(pixel) > 24 &&
        android.graphics.Color.red(pixel) > 224 &&
        android.graphics.Color.green(pixel) > 216 &&
        android.graphics.Color.blue(pixel) > 198 &&
        maxOf(android.graphics.Color.red(pixel), android.graphics.Color.green(pixel), android.graphics.Color.blue(pixel)) -
            minOf(android.graphics.Color.red(pixel), android.graphics.Color.green(pixel), android.graphics.Color.blue(pixel)) < 49
    val queue = IntArray(pixels.size)
    val visited = BooleanArray(pixels.size)
    var head = 0
    var tail = 0
    fun offer(p: Int) {
        if (!visited[p] && isPale(pixels[p])) {
            visited[p] = true
            queue[tail++] = p
        }
    }
    for (y in 1 until height - 1) for (x in 1 until width - 1) {
        val p = y * width + x
        if (isPale(pixels[p]) && (
            android.graphics.Color.alpha(pixels[p - 1]) < 24 ||
            android.graphics.Color.alpha(pixels[p + 1]) < 24 ||
            android.graphics.Color.alpha(pixels[p - width]) < 24 ||
            android.graphics.Color.alpha(pixels[p + width]) < 24
        )) offer(p)
    }
    while (head < tail) {
        val p = queue[head++]
        val alpha = android.graphics.Color.alpha(pixels[p])
        pixels[p] = android.graphics.Color.argb(alpha, 255, 242, 218)
        val x = p % width
        val y = p / width
        if (x > 0) offer(p - 1)
        if (x < width - 1) offer(p + 1)
        if (y > 0) offer(p - width)
        if (y < height - 1) offer(p + width)
    }
    return android.graphics.Bitmap.createBitmap(pixels, width, height, android.graphics.Bitmap.Config.ARGB_8888)
}

@Composable
private fun StickerBadge(icon: ImageVector, accent: Color, size: Int) {
    Box(
        modifier = Modifier.size(size.dp).graphicsLayer { shadowElevation = 5.dp.toPx(); shape = DieCutShape; clip = true }
            .background(RetroColors.Card, DieCutShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = accent,
            modifier = Modifier.size((size * 0.65f).dp))
    }
}
