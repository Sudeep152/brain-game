package com.example.projectpuzzle.ui.design

import androidx.compose.ui.graphics.Color

/**
 * Color tokens — docs/design/DESIGN_SYSTEM.md §3 (Marker Doodle, D-019).
 * Do not add a color here without adding it to the design doc first.
 */
object PuzzleColors {
    val Paper = Color(0xFFFBF6EC)
    val PaperDot = Color(0xFFE6DCC8)
    val Surface = Color(0xFFFFFFFF)
    val Ink = Color(0xFF1F1B16)
    val InkSoft = Color(0xFF5E574B)
    val Red = Color(0xFFE5482E)
    val Sun = Color(0xFFFFC93C)
    val Mint = Color(0xFF3DD6A3)
    val Sky = Color(0xFF6EC3FF)

    /** Text on [Red]; only for bold text >= 18.7 sp (contrast 3.7:1). */
    val OnRed = Color(0xFFFFFFFF)
}
