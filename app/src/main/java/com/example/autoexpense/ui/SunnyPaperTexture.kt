package com.example.autoexpense.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import kotlin.random.Random

/** A quiet paper grain behind the interactive sunny-skin components. */
@Composable
fun SunnyPaperTexture(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val random = Random(2048)
        repeat(1100) {
            drawCircle(
                color = if (it % 3 == 0) Color(0xFFB28655).copy(alpha = .055f)
                    else Color(0xFF5B7BA5).copy(alpha = .035f),
                radius = if (it % 7 == 0) 1.1f else .55f,
                center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height)
            )
        }
    }
}

fun Modifier.sunnyPaperGrain(): Modifier = this.drawBehind {
    val random = Random(2048)
    repeat(1100) {
        drawCircle(
            color = if (it % 3 == 0) Color(0xFFB28655).copy(alpha = .06f)
                else Color(0xFF5B7BA5).copy(alpha = .04f),
            radius = if (it % 7 == 0) 1.1f else .55f,
            center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height)
        )
    }
}
