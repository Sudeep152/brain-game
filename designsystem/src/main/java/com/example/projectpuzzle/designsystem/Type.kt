package com.example.projectpuzzle.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

/** Kalam Bold (SIL OFL) — display text only. Covers Latin + Devanagari. */
val KalamFamily = FontFamily(Font(R.font.kalam_bold, FontWeight.Bold))

/**
 * Baloo 2 (SIL OFL) — all UI text. Covers Latin + Devanagari.
 * Static weight files (500/700/800) generated from the official variable font with fontTools:
 * Android ignored the variable `wght` axis and drew everything at the default 400 (seen in T-307 screenshots).
 */
val Baloo2Family = FontFamily(
    Font(R.font.baloo2_medium, FontWeight.Medium),
    Font(R.font.baloo2_bold, FontWeight.Bold),
    Font(R.font.baloo2_extrabold, FontWeight.ExtraBold),
)

/** Type scale — docs/design/DESIGN_SYSTEM.md §4. */
@Immutable
data class PuzzleTypography(
    val display: TextStyle,
    val stat: TextStyle,
    val levelNumber: TextStyle,
    val title: TextStyle,
    val headline: TextStyle,
    val button: TextStyle,
    val buttonSmall: TextStyle,
    val body: TextStyle,
    val link: TextStyle,
    val segment: TextStyle,
    val codeDigit: TextStyle,
    val meta: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
    /** Game-piece type (Doodle Kit G1/G3/C3/C4): number token, word token, stamp & letter slots, feedback bubble. */
    val tokenNumber: TextStyle,
    val tokenWord: TextStyle,
    val stamp: TextStyle,
    val bubble: TextStyle,
) {
    /** Devanagari needs taller lines (design doc §4: x1.15). */
    fun withLineHeightScale(scale: Float): PuzzleTypography = copy(
        display = display.copy(lineHeight = display.lineHeight * scale),
        stat = stat.copy(lineHeight = stat.lineHeight * scale),
        levelNumber = levelNumber.copy(lineHeight = levelNumber.lineHeight * scale),
        title = title.copy(lineHeight = title.lineHeight * scale),
        headline = headline.copy(lineHeight = headline.lineHeight * scale),
        button = button.copy(lineHeight = button.lineHeight * scale),
        buttonSmall = buttonSmall.copy(lineHeight = buttonSmall.lineHeight * scale),
        body = body.copy(lineHeight = body.lineHeight * scale),
        link = link.copy(lineHeight = link.lineHeight * scale),
        segment = segment.copy(lineHeight = segment.lineHeight * scale),
        codeDigit = codeDigit.copy(lineHeight = codeDigit.lineHeight * scale),
        meta = meta.copy(lineHeight = meta.lineHeight * scale),
        label = label.copy(lineHeight = label.lineHeight * scale),
        caption = caption.copy(lineHeight = caption.lineHeight * scale),
        tokenNumber = tokenNumber.copy(lineHeight = tokenNumber.lineHeight * scale),
        tokenWord = tokenWord.copy(lineHeight = tokenWord.lineHeight * scale),
        stamp = stamp.copy(lineHeight = stamp.lineHeight * scale),
        bubble = bubble.copy(lineHeight = bubble.lineHeight * scale),
    )
}

val DefaultPuzzleTypography = PuzzleTypography(
    display = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 58.sp, lineHeight = 56.sp),
    stat = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 40.sp),
    levelNumber = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 32.sp),
    title = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 37.sp),
    headline = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 27.sp),
    button = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.ExtraBold, fontSize = 23.sp, lineHeight = 26.sp),
    buttonSmall = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 22.sp),
    body = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 23.sp),
    link = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp, textDecoration = TextDecoration.Underline),
    segment = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 20.sp),
    codeDigit = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 38.sp),
    meta = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 18.sp),
    label = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 1.sp),
    caption = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 17.sp),
    tokenNumber = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 44.sp),
    tokenWord = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 32.sp),
    stamp = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 28.sp),
    bubble = TextStyle(fontFamily = Baloo2Family, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, lineHeight = 22.sp),
)
