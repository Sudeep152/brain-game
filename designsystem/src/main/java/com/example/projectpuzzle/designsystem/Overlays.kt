package com.example.projectpuzzle.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------------------------------------
// FeedbackBubble (Doodle Kit C4)
// ---------------------------------------------------------------------------------------------

enum class FeedbackKind { Nope, Close, Yes }

/**
 * Short reaction that pops near the tap for 1.2 s and never blocks input (the caller places and times it).
 * Nope = white + red cross (with object shake + light haptic) · Close = sun + squiggle · Yes = mint + tick.
 * Announced politely to TalkBack.
 */
@Composable
fun FeedbackBubble(kind: FeedbackKind, text: String, modifier: Modifier = Modifier) {
    val fill = when (kind) {
        FeedbackKind.Nope -> PuzzleColors.Surface
        FeedbackKind.Close -> PuzzleColors.Sun
        FeedbackKind.Yes -> PuzzleColors.Mint
    }
    val glyph = when (kind) {
        FeedbackKind.Nope -> Doodles.FeedbackNope
        FeedbackKind.Close -> Doodles.FeedbackClose
        FeedbackKind.Yes -> Doodles.FeedbackYes
    }
    val shape = RoundedCornerShape(Radii.Button)
    Row(
        modifier = modifier
            .semantics { liveRegion = LiveRegionMode.Polite }
            .height(Sizes.FeedbackHeight)
            .clip(shape)
            .background(fill)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape)
            .padding(horizontal = Spacing.L + 2.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DoodleArt(doodle = glyph, modifier = Modifier.width(Sizes.IconGlyph))
        Text(text = text, style = PuzzleTheme.type.bubble)
    }
}

// ---------------------------------------------------------------------------------------------
// Sheet, scrim, sticky note, hint sheet (Doodle Kit C4 / S3)
// ---------------------------------------------------------------------------------------------

/** 40% ink scrim behind sheets and dialogs. Tapping it calls [onDismiss] (if given). */
@Composable
fun Scrim(modifier: Modifier = Modifier, onDismiss: (() -> Unit)? = null) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PuzzleColors.Scrim)
            .then(
                if (onDismiss != null) Modifier.clickable(interactionSource = source, indication = null, onClick = onDismiss) else Modifier,
            ),
    )
}

/**
 * Bottom sheet surface: white, 26 dp top corners, ink outline on top and sides (none at the bottom),
 * ink handle. Padding 12 / 22 / 28, gap 16. Slides up 200 ms over a [Scrim] (caller animates).
 */
@Composable
fun InkSheet(
    modifier: Modifier = Modifier,
    paneTitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val radius = Radii.Card
    val shape = RoundedCornerShape(topStart = radius, topEnd = radius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (paneTitle != null) this.paneTitle = paneTitle }
            .clip(shape)
            .background(PuzzleColors.Surface)
            .drawBehind {
                // Outline drawn taller than the sheet so its bottom edge falls outside (clipped) = no bottom border.
                val stroke = Sizes.OutlineUi.toPx()
                val r = radius.toPx()
                drawRoundRect(
                    color = PuzzleColors.Ink,
                    topLeft = Offset(stroke / 2f, stroke / 2f),
                    size = Size(size.width - stroke, size.height + r * 2f),
                    cornerRadius = CornerRadius(r - stroke / 2f),
                    style = Stroke(width = stroke),
                )
            }
            .padding(
                start = ScreenLayout.Horizontal,
                end = ScreenLayout.Horizontal,
                top = Spacing.M,
                bottom = ScreenLayout.Bottom,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.L),
    ) {
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = Sizes.SheetHandleWidth, height = Sizes.SheetHandleHeight)
                .background(PuzzleColors.Ink, RoundedCornerShape(Sizes.SheetHandleHeight / 2)),
        )
        content()
    }
}

/** Sun sticky note, tilted −1°: holds hint text (Doodle Kit C4 / S3). */
@Composable
fun StickyNote(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(Radii.Sticky)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = -1f }
            .background(PuzzleColors.Sun, shape)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape)
            .padding(horizontal = Spacing.L, vertical = Spacing.M + 2.dp),
    ) {
        Text(text = text, style = PuzzleTheme.type.body)
    }
}

/**
 * Hint sheet: "Hint 2 of 3" + step dots + sticky note, then [actions] (e.g. "Show the answer", or
 * "Watch video" + "Not now" + ad note). Put it at the bottom of a Box over a [Scrim].
 */
@Composable
fun HintSheet(
    title: String,
    used: Int,
    total: Int,
    stepsDescription: String,
    hint: String,
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit = {},
) {
    InkSheet(modifier = modifier, paneTitle = title) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, style = PuzzleTheme.type.headline)
            StepDots(done = used, total = total, contentDescription = stepsDescription)
        }
        StickyNote(text = hint)
        actions()
    }
}

// ---------------------------------------------------------------------------------------------
// Dialog card & rewarded prompt (Doodle Kit C4)
// ---------------------------------------------------------------------------------------------

/**
 * Confirm dialog card: title, body, max 2 actions in a row. Destructive actions are never red-filled;
 * red stays for "Play / Next". Show it centered over a [Scrim].
 */
@Composable
fun InkDialogCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit,
) {
    InkCard(
        fill = PuzzleColors.Surface,
        contentPadding = PaddingValues(horizontal = Spacing.XL, vertical = Spacing.XXL),
        verticalArrangement = Arrangement.spacedBy(Spacing.M),
        modifier = modifier.semantics { paneTitle = title },
    ) {
        Text(text = title, style = PuzzleTheme.type.title)
        Text(text = body, style = PuzzleTheme.type.body)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.XS + 2.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.M),
            content = actions,
        )
    }
}

/**
 * Ask before any rewarded video: sky card (play tile + title + body) and below it "Watch" + "Not now".
 * Never auto-play; "Not now" is always visible.
 */
@Composable
fun RewardedPrompt(
    title: String,
    body: String,
    watchText: String,
    onWatch: () -> Unit,
    notNowText: String,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.L)) {
        InkCard(
            fill = PuzzleColors.Sky,
            radius = Radii.CardSmall,
            contentPadding = PaddingValues(Spacing.L),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp), verticalAlignment = Alignment.CenterVertically) {
                val shape = RoundedCornerShape(Radii.IconButton)
                Box(
                    modifier = Modifier
                        .size(Sizes.PromptIcon)
                        .clip(shape)
                        .background(PuzzleColors.Surface)
                        .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = PuzzleIcons.PlayFilled, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.IconGlyph))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = title, style = PuzzleTheme.type.meta)
                    Text(text = body, style = PuzzleTheme.type.caption, color = PuzzleColors.Ink)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.L),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PressableButton(text = watchText, onClick = onWatch, modifier = Modifier.weight(1f))
            TextLink(text = notNowText, onClick = onNotNow)
        }
    }
}

/** Caption text in inkSoft (the caption style carries no color). */
@Composable
fun CaptionText(text: String, modifier: Modifier = Modifier, color: Color = PuzzleColors.InkSoft, center: Boolean = false) {
    Text(
        text = text,
        style = PuzzleTheme.type.caption,
        color = color,
        textAlign = if (center) TextAlign.Center else TextAlign.Start,
        modifier = modifier,
    )
}
