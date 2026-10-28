package com.example.autoexpense.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.autoexpense.R
import kotlin.random.Random

object RetroColors {
    val Ink = Color(0xFF26170F)
    val InkSoft = Color(0xFF493123)
    val Paper = Color(0xFFFFF2DA)
    val PaperDeep = Color(0xFFF1DFC1)
    val Card = Color(0xFFFFF6E7)
    val Line = Color(0xFF8F6A4C)
    val Muted = Color(0xFF816C57)
    val Gold = Color(0xFFFFB638)
    val Red = Color(0xFFC65A25)
    val Green = Color(0xFF4E8B55)
    val Blue = Color(0xFF3B78A0)
}

/** A shallow, irregular paper tear. The points are fixed so the edge never jumps during recomposition. */
val TornPaperShape = GenericShape { size, _ ->
    val sections = 16
    val step = size.width / sections
    fun heightAt(i: Int): Float {
        val broad = floatArrayOf(25f, 21f, 19f, 22f, 13f, 15f, 11f, 17f, 14f, 20f, 16f, 22f, 19f, 24f, 17f, 21f, 25f)
        return broad[i]
    }
    moveTo(0f, heightAt(0))
    for (i in 0 until sections) {
        val nextX = (i + 1) * step
        val nextY = heightAt(i + 1)
        quadraticBezierTo(nextX - step * 0.38f, heightAt(i), nextX, nextY)
    }
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

/** Broad cloud lobes with small irregular joins, used by the confirmation sheet. */
val CloudTornPaperShape = GenericShape { size, _ ->
    val fractions = floatArrayOf(0f, .12f, .26f, .38f, .56f, .68f, .83f, 1f)
    val valleys = floatArrayOf(35f, 43f, 32f, 44f, 34f, 42f, 33f, 39f)
    val peaks = floatArrayOf(9f, 2f, 12f, 4f, 8f, 1f, 11f)
    moveTo(0f, valleys[0])
    for (i in peaks.indices) {
        val x = fractions[i] * size.width
        val nextX = fractions[i + 1] * size.width
        val step = nextX - x
        cubicTo(x + step * .15f, valleys[i] - 5f,
            x + step * .20f, peaks[i], x + step * .50f, peaks[i])
        cubicTo(x + step * .80f, peaks[i] + 2f,
            x + step * .84f, valleys[i + 1] - 6f, nextX, valleys[i + 1])
        if (i < peaks.lastIndex) lineTo(nextX + 2f, valleys[i + 1] + 2f)
    }
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

private val StickerCutShape = GenericShape { size, _ ->
    val w = size.width; val h = size.height
    moveTo(w * .16f, h * .04f)
    lineTo(w * .42f, 0f); lineTo(w * .68f, h * .03f)
    lineTo(w * .89f, h * .12f); lineTo(w, h * .37f)
    lineTo(w * .96f, h * .69f); lineTo(w * .84f, h * .95f)
    lineTo(w * .51f, h); lineTo(w * .22f, h * .97f)
    lineTo(0f, h * .75f); lineTo(w * .03f, h * .41f)
    close()
}

@Composable
fun ThemeStickerIcon(icon: ImageVector, accent: Color, size: Dp = 42.dp) {
    if (LocalAppSkin.current == AppSkin.Mono) {
        Box(Modifier.size(size).background(monoPalette.paperDeep, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = monoPalette.ink, modifier = Modifier.size(size * .58f))
        }
        return
    }
    if (LocalAppSkin.current == AppSkin.Folk) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(size * .72f))
        return
    }
    if (LocalAppSkin.current == AppSkin.Sunny) {
        Box(
            modifier = Modifier.size(size).background(accent.copy(alpha = 0.14f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(size * .62f))
        }
        return
    }
    Box(
        modifier = Modifier.size(size).shadow(3.dp, StickerCutShape).background(RetroColors.Card, StickerCutShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(size * .76f).background(accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = RetroColors.Card, modifier = Modifier.size(size * .48f))
        }
    }
}

@Composable
fun PaperTexture(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val spacing = 11.dp.toPx()
        var y = 13.dp.toPx()
        while (y < size.height) {
            var x = 7.dp.toPx()
            while (x < size.width) {
                drawCircle(
                    color = RetroColors.Line.copy(alpha = 0.055f),
                    radius = 0.55.dp.toPx(),
                    center = Offset(x, y)
                )
                x += spacing
            }
            y += spacing
        }
    }
}

@Composable
fun TornPaperPanel(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.clip(TornPaperShape).background(RetroColors.Paper)) {
        PaperTexture()
        content()
    }
}

private val retroScheme: ColorScheme = darkColorScheme(
    primary = RetroColors.Gold,
    onPrimary = RetroColors.Ink,
    secondary = RetroColors.Red,
    onSecondary = RetroColors.Card,
    background = RetroColors.Ink,
    onBackground = RetroColors.Card,
    surface = RetroColors.Card,
    onSurface = RetroColors.Ink,
    surfaceVariant = RetroColors.PaperDeep,
    onSurfaceVariant = RetroColors.Muted,
    outline = RetroColors.Line,
    error = RetroColors.Red,
    onError = RetroColors.Card
)

private val sunnyScheme: ColorScheme = androidx.compose.material3.lightColorScheme(
    primary = sunnyPalette.primary,
    onPrimary = Color.White,
    secondary = sunnyPalette.secondary,
    onSecondary = sunnyPalette.ink,
    background = sunnyPalette.background,
    onBackground = sunnyPalette.ink,
    surface = sunnyPalette.card,
    onSurface = sunnyPalette.ink,
    surfaceVariant = sunnyPalette.paperDeep,
    onSurfaceVariant = sunnyPalette.muted,
    outline = sunnyPalette.line,
    error = Color(0xFFF45B58)
)

private val folkScheme: ColorScheme = androidx.compose.material3.lightColorScheme(
    primary = folkPalette.primary, onPrimary = Color.White,
    secondary = folkPalette.secondary, onSecondary = folkPalette.ink,
    background = folkPalette.background, onBackground = folkPalette.ink,
    surface = folkPalette.card, onSurface = folkPalette.ink,
    surfaceVariant = folkPalette.paperDeep, onSurfaceVariant = folkPalette.muted,
    outline = folkPalette.line, error = Color(0xFFF5544D)
)

private val monoScheme: ColorScheme = androidx.compose.material3.lightColorScheme(
    primary = monoPalette.primary, onPrimary = Color.White,
    secondary = monoPalette.secondary, onSecondary = Color.White,
    background = monoPalette.background, onBackground = monoPalette.ink,
    surface = monoPalette.card, onSurface = monoPalette.ink,
    surfaceVariant = monoPalette.paperDeep, onSurfaceVariant = monoPalette.muted,
    outline = monoPalette.line, error = monoPalette.secondary
)

private val retroTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 31.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 27.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 23.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

private val readableTypography = Typography(
    headlineLarge = retroTypography.headlineLarge.copy(fontFamily = FontFamily.Default),
    headlineMedium = retroTypography.headlineMedium.copy(fontFamily = FontFamily.Default),
    titleLarge = retroTypography.titleLarge.copy(fontFamily = FontFamily.Default),
    titleMedium = retroTypography.titleMedium.copy(fontFamily = FontFamily.Default),
    bodyLarge = retroTypography.bodyLarge.copy(fontFamily = FontFamily.Default),
    bodyMedium = retroTypography.bodyMedium.copy(fontFamily = FontFamily.Default),
    labelLarge = retroTypography.labelLarge.copy(fontFamily = FontFamily.Default),
    labelMedium = retroTypography.labelMedium.copy(fontFamily = FontFamily.Default)
)

@Composable
fun ReadableRetroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = when (LocalAppSkin.current) { AppSkin.Sunny -> sunnyScheme; AppSkin.Folk -> folkScheme; AppSkin.Mono -> monoScheme; else -> retroScheme },
        typography = readableTypography,
        content = content
    )
}

@Composable
fun AutoExpenseRetroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = when (LocalAppSkin.current) { AppSkin.Sunny -> sunnyScheme; AppSkin.Folk -> folkScheme; AppSkin.Mono -> monoScheme; else -> retroScheme },
        typography = retroTypography,
        content = content
    )
}

@Composable
fun RetroPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val sunny = LocalAppSkin.current == AppSkin.Sunny
    val folk = LocalAppSkin.current == AppSkin.Folk
    val mono = LocalAppSkin.current == AppSkin.Mono
    val palette = currentSkinPalette()
    Card(
        modifier = modifier,
        shape = if (sunny || folk || mono) RoundedCornerShape(if (folk) 24.dp else if (mono) 18.dp else 17.dp) else TornPaperShape,
        colors = CardDefaults.cardColors(containerColor = if (sunny || folk || mono) palette.card else RetroColors.Paper),
        border = if (sunny || folk || mono) BorderStroke(1.dp, palette.line.copy(alpha = .72f)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    if (folk || mono) return@drawBehind
                    if (sunny) {
                        val random = Random(2048)
                        repeat(650) {
                            drawCircle(
                                palette.line.copy(alpha = .10f),
                                radius = if (it % 6 == 0) 1.1f else .55f,
                                center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height)
                            )
                        }
                        return@drawBehind
                    }
                    val step = 12.dp.toPx()
                    var y = step
                    while (y < size.height) {
                        var x = step
                        while (x < size.width) {
                            drawCircle(RetroColors.Line.copy(alpha = 0.045f), 0.5.dp.toPx(), Offset(x, y))
                            x += step
                        }
                        y += step
                    }
                }
                .padding(start = 16.dp, end = 16.dp,
                    top = if (sunny || folk || mono) 14.dp else 22.dp,
                    bottom = if (sunny || folk || mono) 14.dp else 16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun RetroPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val sunny = LocalAppSkin.current != AppSkin.Caramel
    val mono = LocalAppSkin.current == AppSkin.Mono
    val palette = currentSkinPalette()
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(if (mono) 50.dp else 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.primary,
            contentColor = Color.White
        ),
        border = if (sunny) null else BorderStroke(2.dp, RetroColors.Gold),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 13.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        content = content
    )
}

@Composable
fun RetroSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val sunny = LocalAppSkin.current != AppSkin.Caramel
    val mono = LocalAppSkin.current == AppSkin.Mono
    val palette = currentSkinPalette()
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(if (mono) 50.dp else 12.dp),
        border = BorderStroke(1.dp, if (sunny) palette.line else RetroColors.Line.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (sunny) palette.paperDeep else RetroColors.Paper,
            contentColor = palette.ink
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 13.dp),
        content = content
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = currentSkinPalette().muted
    )
}

fun Modifier.retroSurface(): Modifier = this.background(RetroColors.Ink)
