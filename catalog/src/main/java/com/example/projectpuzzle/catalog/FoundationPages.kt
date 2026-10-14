package com.example.projectpuzzle.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.projectpuzzle.designsystem.CaptionText
import com.example.projectpuzzle.designsystem.DoodleArt
import com.example.projectpuzzle.designsystem.DoodleLoader
import com.example.projectpuzzle.designsystem.Doodles
import com.example.projectpuzzle.designsystem.InkCard
import com.example.projectpuzzle.designsystem.LabelText
import com.example.projectpuzzle.designsystem.PressableButton
import com.example.projectpuzzle.designsystem.PuzzleButtonStyle
import com.example.projectpuzzle.designsystem.PuzzleColors
import com.example.projectpuzzle.designsystem.PuzzleIcons
import com.example.projectpuzzle.designsystem.PuzzleTheme
import com.example.projectpuzzle.designsystem.Radii
import com.example.projectpuzzle.designsystem.Sizes
import com.example.projectpuzzle.designsystem.Spacing

// ---------------------------------------------------------------------------------------------
// Colors (canvas F1)
// ---------------------------------------------------------------------------------------------

private data class Swatch(val name: String, val hex: String, val color: Color, val use: String)

private val swatches = listOf(
    Swatch("paper", "#FBF6EC", PuzzleColors.Paper, "Screen background, with dotted grid"),
    Swatch("paperDot", "#E6DCC8", PuzzleColors.PaperDot, "Dot grid only (1 dp dots, 22 dp apart); row dividers"),
    Swatch("surface", "#FFFFFF", PuzzleColors.Surface, "Puzzle board, secondary buttons, inputs"),
    Swatch("ink", "#1F1B16", PuzzleColors.Ink, "Outlines, text, icons, press edge"),
    Swatch("inkSoft", "#5E574B", PuzzleColors.InkSoft, "Secondary text only (6.4:1 on paper)"),
    Swatch("red", "#E5482E", PuzzleColors.Red, "Primary action, progress, \"nope\". White text only bold ≥ 18.7 px"),
    Swatch("sun", "#FFC93C", PuzzleColors.Sun, "Hero card, hint button, highlight"),
    Swatch("mint", "#3DD6A3", PuzzleColors.Mint, "Daily puzzle, success / \"yes\""),
    Swatch("sky", "#6EC3FF", PuzzleColors.Sky, "Settings, info, ice / water objects"),
    Swatch("wood", "#E8B66A", PuzzleColors.Wood, "Matchsticks only (D-021)"),
    Swatch("scrim", "ink · 40%", PuzzleColors.Scrim, "Behind sheets and dialogs"),
)

@Composable
fun ColorsPage(onBack: () -> Unit) {
    KitPage(title = "Colors", kicker = "Doodle Kit · Foundations", onBack = onBack) {
        swatches.forEach { swatch ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp), verticalAlignment = Alignment.CenterVertically) {
                val shape = RoundedCornerShape(Radii.CardSmall)
                Box(
                    Modifier
                        .size(width = Sizes.LevelTile + Spacing.XXL, height = Sizes.LevelTile)
                        .clip(shape)
                        .background(swatch.color)
                        .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "${swatch.name}  ${swatch.hex}", style = PuzzleTheme.type.meta)
                    CaptionText(swatch.use)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Type scale (canvas F1)
// ---------------------------------------------------------------------------------------------

@Composable
fun TypePage(onBack: () -> Unit) {
    val t = PuzzleTheme.type
    // Same order and sample text as the canvas F1 type table.
    val styles: List<Triple<String, String, Pair<TextStyle, String>>> = listOf(
        Triple("display", "Kalam 700 · 58 / 56", t.display to tr("Level 7", "लेवल 7")),
        Triple("stat", "Kalam 700 · 38 / 40", t.stat to tr("3", "३ दिन")),
        Triple("title", "Kalam 700 · 32 / 37", t.title to tr("Melt the ice.", "बर्फ़ पिघलाओ।")),
        Triple("headline", "Kalam 700 · 24 / 27", t.headline to tr("Odd one out", "अलग कौन है?")),
        Triple("button", "Baloo 2 800 · 23 / 26", t.button to tr("Play", "खेलें")),
        Triple("buttonSmall", "Baloo 2 700 · 18 / 22", t.buttonSmall to tr("Stuck? Get a hint", "अटक गए? संकेत लें")),
        Triple("body", "Baloo 2 500 · 17 / 23", t.body to tr("Fire, ice and one clever move.", "आग, बर्फ़ और एक चतुर चाल।")),
        Triple("meta", "Baloo 2 700 · 15 / 18", t.meta to tr("Chapter 1 · 7 of 40", "अध्याय 1 · 40 में से 7")),
        Triple("link", "Baloo 2 700 · 16 / 20 · underline", t.link to tr("Play today's", "आज की पहेली खेलें")),
        Triple("label", "Baloo 2 700 · 13 / 16 · +1 · CAPS (Latin only)", t.label to tr("Daily puzzle", "आज की पहेली")),
        Triple("caption", "Baloo 2 500 · 13 / 17 · inkSoft", t.caption to tr("Hints unlock after a short video.", "छोटा वीडियो देखने के बाद संकेत मिलता है।")),
        Triple("levelNumber", "Kalam 700 · 30 / 32", t.levelNumber to tr("7", "७")),
        Triple("segment", "Baloo 2 700 · 17 / 20", t.segment to tr("English", "हिंदी")),
        Triple("codeDigit", "Kalam 700 · 34 / 38", t.codeDigit to tr("4 2", "४ २")),
        Triple("tokenNumber", "Kalam 700 · 40 / 44 · number token", t.tokenNumber to tr("9", "९")),
        Triple("tokenWord", "Kalam 700 · 28 / 32 · word token", t.tokenWord to tr("number", "संख्या")),
        Triple("stamp", "Kalam 700 · 26 / 28 · stamp, letter slots", t.stamp to tr("DONE!", "हो गया!")),
        Triple("bubble", "Baloo 2 800 · 18 / 22 · feedback bubble", t.bubble to tr("Not quite!", "नहीं!")),
    )
    KitPage(title = "Type scale", kicker = "Kalam = voice · Baloo 2 = reading", onBack = onBack) {
        KitBoard {
            styles.forEach { (name, spec, pair) ->
                val (style, text) = pair
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = name, style = t.meta)
                    CaptionText(spec)
                    if (name == "label") {
                        LabelText(text)
                    } else {
                        Text(text = text, style = style, color = if (name == "caption") PuzzleColors.InkSoft else PuzzleColors.Ink)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Space, shape, icons, motion (canvas F2)
// ---------------------------------------------------------------------------------------------

@Composable
fun SpacePage(onBack: () -> Unit) {
    KitPage(title = "Space & shape", kicker = "Doodle Kit · Foundations", onBack = onBack) {
        KitSection(title = "Spacing · 4 dp grid") {
            listOf(
                "XS 4" to Spacing.XS, "S 8" to Spacing.S, "M 12" to Spacing.M, "L 16" to Spacing.L,
                "XL 20" to Spacing.XL, "XXL 24" to Spacing.XXL, "XXXL 32" to Spacing.XXXL,
            ).forEach { (name, size) -> SpacingBar(name, size) }
            InkCard(
                fill = PuzzleColors.Surface,
                offRegister = false,
                radius = Radii.CardSmall,
                contentPadding = PaddingValues(Spacing.L),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Screen: 22 sides · 20 top · 28 bottom · 20 between sections", style = PuzzleTheme.type.meta)
                Text("Hero card: 24 / 20 / 24, gap 12 · small card: 16, gap 4", style = PuzzleTheme.type.meta)
                Text("Touch targets ≥ 48 dp · puzzle tappables ≥ 124 scene units", style = PuzzleTheme.type.meta)
            }
        }
        KitSection(title = "Corner radii") {
            KitFlow {
                listOf(
                    "chip 11" to Radii.Chip, "icon 16" to Radii.IconButton, "button 18" to Radii.Button,
                    "cardSmall 20" to Radii.CardSmall, "card / board 26" to Radii.Card,
                    "sticky 4" to Radii.Sticky, "highlight 6" to Radii.Highlight,
                ).forEach { (name, radius) ->
                    KitSpec(caption = name) {
                        val shape = RoundedCornerShape(radius)
                        Box(
                            Modifier
                                .size(Sizes.LevelTile)
                                .clip(shape)
                                .background(PuzzleColors.Sun)
                                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
                        )
                    }
                }
            }
        }
        KitSection(title = "Signature devices") {
            KitSpec(caption = "Off-register fill: 6 right + 6 down, same size as outline", alignment = Alignment.Start) {
                InkCard(fill = PuzzleColors.Sky, radius = Radii.CardSmall, modifier = Modifier.size(width = 140.dp, height = 84.dp)) {}
            }
            KitSpec(caption = "Ink outline 2.5 (UI) · 3 (puzzle objects), round joins", alignment = Alignment.Start) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.L)) {
                    DoodleArt(Doodles.circle(PuzzleColors.Red), Modifier.width(Sizes.LevelTile))
                    DoodleArt(Doodles.triangle(PuzzleColors.Mint), Modifier.width(Sizes.LevelTile))
                    DoodleArt(Doodles.Squiggle, Modifier.width(Sizes.LevelTile))
                }
            }
            KitSpec(caption = "Press edge 5 dp ink, no blur. Pressed: face moves down 5, edge hides", alignment = Alignment.Start) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M)) {
                    PressableButton(text = "Normal", onClick = {}, style = PuzzleButtonStyle.Secondary, modifier = Modifier.weight(1f))
                    PressableButton(text = "Pressed", onClick = {}, style = PuzzleButtonStyle.Secondary, showPressed = true, modifier = Modifier.weight(1f))
                }
            }
        }
        KitSection(title = "Icons · 24 grid · 2.4 stroke · round caps") {
            KitFlow {
                PuzzleIcons.all.forEach { (name, icon) ->
                    KitSpec(caption = name, modifier = Modifier.width(Sizes.LevelTile)) {
                        val shape = RoundedCornerShape(Radii.IconButton)
                        Box(
                            modifier = Modifier
                                .size(Sizes.IconButton)
                                .clip(shape)
                                .background(PuzzleColors.Surface)
                                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(imageVector = icon, contentDescription = name, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.IconGlyph))
                        }
                    }
                }
            }
        }
        KitSection(title = "Motion · bouncy, quick, hand-made", caption = "Turn Motion off on the home screen: everything still works, nothing moves.") {
            listOf(
                "Press · 80 ms + spring 0.6" to "Face drops 5 dp onto its edge. Light haptic.",
                "Wobble-in · 220 ms" to "Rotate −3° → 0°, scale 0.92 → 1. Screens and objects appearing.",
                "Drag lift" to "Scale 1.08, rotate −6°, ink ellipse shadow. Light haptic.",
                "Nope shake · 240 ms" to "±6 dp × 3, outline flashes red. Light haptic. No lives lost.",
                "Solved pop · 600 ms" to "1 → 1.15 → 1 + 6–8 doodle confetti. Success haptic.",
            ).forEach { (title, body) ->
                InkCard(
                    fill = PuzzleColors.Surface,
                    offRegister = false,
                    radius = Radii.CardSmall,
                    contentPadding = PaddingValues(Spacing.L),
                    verticalArrangement = Arrangement.spacedBy(Spacing.XS),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = title, style = PuzzleTheme.type.meta)
                    CaptionText(body)
                }
            }
            KitSpec(caption = "Loader · 3 doodles hop in turn", alignment = Alignment.Start) {
                DoodleLoader(contentDescription = "Loading")
            }
        }
    }
}

@Composable
private fun SpacingBar(name: String, size: Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M), verticalAlignment = Alignment.CenterVertically) {
        Text(text = name, style = PuzzleTheme.type.meta, modifier = Modifier.width(Sizes.LevelTile))
        Box(
            Modifier
                .width(size * 4)
                .height(Spacing.L)
                .background(PuzzleColors.Red),
        )
    }
}
