package com.example.projectpuzzle.ui.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.projectpuzzle.R

/** Kalam Bold (SIL OFL) — display text only. Covers Latin + Devanagari. */
val KalamFamily = FontFamily(Font(R.font.kalam_bold, FontWeight.Bold))

/** Baloo 2 variable font (SIL OFL) — all UI text. Covers Latin + Devanagari. */
val Baloo2Family = FontFamily(
    Font(R.font.baloo2_variable, FontWeight.Medium),
    Font(R.font.baloo2_variable, FontWeight.Bold),
    Font(R.font.baloo2_variable, FontWeight.ExtraBold),
)

/** Type scale — docs/design/DESIGN_SYSTEM.md §4. */
@Immutable
data class PuzzleTypography(
    val display: TextStyle,
    val stat: TextStyle,
    val title: TextStyle,
    val headline: TextStyle,
    val button: TextStyle,
    val buttonSmall: TextStyle,
    val body: TextStyle,
    val link: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
) {
    /** Devanagari needs taller lines (design doc §4: x1.15). */
    fun withLineHeightScale(scale: Float): PuzzleTypography = copy(
        display = display.copy(lineHeight = display.lineHeight * scale),
        stat = stat.copy(lineHeight = stat.lineHeight * scale),
        title = title.copy(lineHeight = title.lineHeight * scale),
        headline = headline.copy(lineHeight = headline.lineHeight * scale),
        button = button.copy(lineHeight = button.lineHeight * scale),
        buttonSmall = buttonSmall.copy(lineHeight = buttonSmall.lineHeight * scale),
        body = body.copy(lineHeight = body.lineHeight * scale),
        link = link.copy(lineHeight = link.lineHeight * scale),
        label = label.copy(lineHeight = label.lineHeight * scale),
        caption = caption.copy(lineHeight = caption.lineHeight * scale),
    )
}

val DefaultPuzzleTypography = PuzzleTypography(
    display = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 58.sp, lineHeight = 56.sp),
    stat = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 40.sp),
    title = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 37.sp),
    headline = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 27.sp),
    button = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.ExtraBold, fontSize = 23.sp, lineHeight = 26.sp),
    buttonSmall = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 22.sp),
    body = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 23.sp),
    link = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp, textDecoration = TextDecoration.Underline),
    label = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 1.sp),
    caption = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 17.sp),
)
