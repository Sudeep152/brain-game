package com.example.projectpuzzle.ui.design

import androidx.compose.ui.unit.dp

/** Spacing scale (4 dp grid) — docs/design/DESIGN_SYSTEM.md §5. */
object Spacing {
    val XS = 4.dp
    val S = 8.dp
    val M = 12.dp
    val L = 16.dp
    val XL = 20.dp
    val XXL = 24.dp
    val XXXL = 32.dp
}

/** Screen layout — §5. */
object ScreenLayout {
    val Horizontal = 22.dp
    val Top = 20.dp
    val Bottom = 28.dp
    val SectionGap = 20.dp
}

/** Sizes and strokes — §2, §5. */
object Sizes {
    val MinTouch = 48.dp
    val IconButton = 48.dp
    val IconGlyph = 22.dp
    val ButtonHeight = 60.dp
    val ButtonSmallHeight = 58.dp
    val PressEdge = 5.dp
    val OffRegister = 6.dp
    val OutlineUi = 2.5.dp
    val OutlineObject = 3.dp
    val DotGrid = 22.dp
    val DotRadius = 1.dp
    val ProgressHeight = 20.dp
    val Badge = 22.dp
    val BadgeBorder = 2.dp
    val StreakCardWidth = 116.dp
    val HeroDoodleWidth = 96.dp
    val HeroDoodleHeight = 80.dp

    /** Minimum tappable size inside a 1000-unit-wide puzzle scene (~48 dp on a 390 dp screen). */
    const val PuzzleMinTouchUnits = 124
}

/** Corner radii — §5. */
object Radii {
    val Chip = 11.dp
    val IconButton = 16.dp
    val Button = 18.dp
    val Card = 26.dp
    val CardSmall = 20.dp
    val Board = 26.dp
}

/** Motion specs — §7. Everything must also work with animations turned off. */
object Motion {
    const val PressMs = 80
    const val AppearMs = 220
    const val NopeMs = 240
    const val SolvedMs = 600
    const val HintMs = 200
    const val PressDampingRatio = 0.6f
    const val AppearRotationDeg = -3f
    const val AppearScaleFrom = 0.92f
    const val DragLiftScale = 1.08f
    const val DragLiftRotationDeg = -6f
    const val SolvedPopScale = 1.15f
    val NopeShake = 6.dp
}
