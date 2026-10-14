package com.example.projectpuzzle.designsystem

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
    val BadgeGlyph = 10.dp
    val BusyDot = 8.dp
    val ChipHeight = 34.dp
    val ChipGlyph = 16.dp
    val LoaderPiece = 32.dp
    val LoaderHop = 12.dp
    val EmptyArt = 80.dp
    val SwitchWidth = 56.dp
    val SwitchHeight = 32.dp
    val SwitchKnob = 21.dp
    val SwitchInset = 0.5.dp
    val ChevronGlyph = 18.dp
    val Divider = 2.dp
    val ErrorDot = 18.dp
    val CheckBoxRadius = 8.dp
    val RadioDot = 12.dp
    val LetterTileRadius = 10.dp
    val SegmentedHeight = 52.dp
    val TextFieldHeight = 56.dp
    val CheckBox = 26.dp
    val SettingRowHeight = 64.dp
    val LevelTile = 72.dp
    val CodeBoxWidth = 58.dp
    val CodeBoxHeight = 66.dp
    val LetterTile = 30.dp
    val SheetHandleWidth = 44.dp
    val SheetHandleHeight = 6.dp
    val StepDot = 14.dp
    val FeedbackHeight = 52.dp
    val DeviceCueTile = 120.dp
    val SolvedBadge = 160.dp
    val OddTile = 64.dp
    val NumberToken = 84.dp
    val StreakCardWidth = 116.dp
    val HeroDoodleWidth = 96.dp
    val HeroDoodleHeight = 80.dp

    /** Minimum tappable size inside a 1000-unit-wide puzzle scene (~48 dp on a 390 dp screen). */
    const val PuzzleMinTouchUnits = 124

    // Game pieces, overlays, celebration (Doodle Kit G1–G3, C3, C4)
    val RowIcon = 24.dp
    val SliderTrack = 14.dp
    val SliderThumb = 32.dp
    val LetterSlotWidth = 44.dp
    val LetterSlotHeight = 52.dp
    val PromptIcon = 52.dp
    val PrimerIcon = 64.dp
    val PrimerGlyph = 30.dp
    val SolvedGlyph = 80.dp
    val BurstHeight = 240.dp
    val Stamp = 120.dp
    val StampBorder = 5.dp
    val StreakStar = 90.dp
    val Sticker = 110.dp
    val StickerBorder = 6.dp
    val StickerArt = 60.dp
    val DeviceCueArt = 90.dp
    val OddTileArt = 40.dp
    val MatchstickWidth = 60.dp
    val MatchstickHeight = 110.dp
    val PuzzleObjectPreview = 100.dp
}

/** Corner radii — §5. */
object Radii {
    val Chip = 11.dp
    val IconButton = 16.dp
    val Button = 18.dp
    val Card = 26.dp
    val CardSmall = 20.dp
    val Board = 26.dp
    /** Hint sticky note (C4/S3). */
    val Sticky = 4.dp
    /** TextLink pressed highlight (C1). */
    val Highlight = 6.dp
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
