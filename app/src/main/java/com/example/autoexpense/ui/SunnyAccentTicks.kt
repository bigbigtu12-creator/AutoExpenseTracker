package com.example.autoexpense.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun SunnyAccentTicks(modifier: Modifier = Modifier) {
    Canvas(modifier.size(25.dp)) {
        val ink = Color(0xFFF45B58)
        drawLine(ink, Offset(size.width * .29f, size.height * .43f),
            Offset(size.width * .50f, size.height * .10f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        drawLine(ink, Offset(size.width * .55f, size.height * .77f),
            Offset(size.width * .90f, size.height * .56f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
    }
}
