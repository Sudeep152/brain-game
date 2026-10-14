package com.example.projectpuzzle.designsystem

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Fact chip: radius 11, 34 high, never tappable (Doodle Kit C2). */
@Composable
fun InkChip(
    text: String,
    modifier: Modifier = Modifier,
    fill: Color = PuzzleColors.Surface,
    leadingIcon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(Radii.Chip)
    Row(
        modifier = modifier
            .height(Sizes.ChipHeight)
            .clip(shape)
            .background(fill)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape)
            .padding(horizontal = Spacing.M),
        horizontalArrangement = Arrangement.spacedBy(Spacing.XS + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.ChipGlyph))
        }
        Text(text = text, style = PuzzleTheme.type.meta)
    }
}

enum class LevelTileState { Solved, Current, Unlocked, Locked }

/** 72 dp level tile in four states (Doodle Kit C2 / S1). [contentDescription] e.g. "Level 6, solved". */
@Composable
fun LevelTile(
    number: Int,
    state: LevelTileState,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val type = PuzzleTheme.type
    when (state) {
        LevelTileState.Current -> PressableSurface(
            onClick = onClick,
            fill = PuzzleColors.Sun,
            height = Sizes.LevelTile,
            radius = Radii.CardSmall,
            modifier = modifier
                .width(Sizes.LevelTile)
                .semantics { this.contentDescription = contentDescription },
        ) { Text(text = number.toString(), style = type.levelNumber) }

        LevelTileState.Solved, LevelTileState.Unlocked -> Box(modifier = modifier.size(Sizes.LevelTile + Sizes.PressEdge)) {
            InkCard(
                fill = if (state == LevelTileState.Solved) PuzzleColors.Mint else PuzzleColors.Surface,
                radius = Radii.CardSmall,
                contentPadding = PaddingValues(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(Sizes.LevelTile)
                    .semantics(mergeDescendants = true) { this.contentDescription = contentDescription }
                    .clickable(role = Role.Button, onClick = onClick),
            ) { Text(text = number.toString(), style = type.levelNumber) }
            if (state == LevelTileState.Solved) {
                Box(
                    modifier = Modifier
                        .offset(x = Sizes.LevelTile - Sizes.Badge + Sizes.OffRegister, y = Sizes.LevelTile - Sizes.Badge + Sizes.OffRegister)
                        .size(Sizes.Badge)
                        .clip(CircleShape)
                        .background(PuzzleColors.Ink),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = PuzzleIcons.Check, contentDescription = null, tint = PuzzleColors.Surface, modifier = Modifier.size(14.dp))
                }
            }
        }

        LevelTileState.Locked -> Box(
            modifier = modifier
                .size(Sizes.LevelTile)
                .semantics { this.contentDescription = contentDescription }
                .drawBehind {
                    val stroke = Sizes.OutlineUi.toPx()
                    drawRoundRect(
                        color = PuzzleColors.InkSoft,
                        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                        cornerRadius = CornerRadius(Radii.CardSmall.toPx()),
                        style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = PuzzleIcons.Lock, contentDescription = null, tint = PuzzleColors.InkSoft, modifier = Modifier.size(Sizes.IconGlyph))
        }
    }
}

/** Label + big Kalam value on a flat white small card (Doodle Kit G3 / S2). */
@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    InkCard(
        fill = PuzzleColors.Surface,
        offRegister = false,
        radius = Radii.CardSmall,
        contentPadding = PaddingValues(horizontal = Spacing.L, vertical = Spacing.M + 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier,
    ) {
        LabelText(label)
        Text(text = value, style = PuzzleTheme.type.stat)
    }
}

/** Status banner, e.g. offline (Doodle Kit C4 / S7). */
@Composable
fun StatusBanner(icon: ImageVector, text: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    InkCard(
        fill = PuzzleColors.Surface,
        offRegister = false,
        radius = Radii.CardSmall,
        contentPadding = PaddingValues(horizontal = Spacing.L - 2.dp, vertical = Spacing.M),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.IconGlyph))
            Text(text = text, style = PuzzleTheme.type.meta, modifier = Modifier.weight(1f))
            action?.invoke()
        }
    }
}

/** Hint progress dots: [done] filled sun of [total] (Doodle Kit C4). */
@Composable
fun StepDots(done: Int, total: Int, contentDescription: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        horizontalArrangement = Arrangement.spacedBy(Spacing.XS + 2.dp),
    ) {
        repeat(total) { index ->
            Box(
                Modifier
                    .size(Sizes.StepDot)
                    .clip(CircleShape)
                    .background(if (index < done) PuzzleColors.Sun else PuzzleColors.Surface)
                    .border(Sizes.OutlineUi, PuzzleColors.Ink, CircleShape),
            )
        }
    }
}

/** Loader: red circle, sun square, mint triangle hop in turn; static when animations are off. */
@Composable
fun DoodleLoader(contentDescription: String, modifier: Modifier = Modifier) {
    val reduceMotion = LocalReduceMotion.current
    val phase = if (reduceMotion) {
        -1f
    } else {
        val transition = rememberInfiniteTransition(label = "loader")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 1200), RepeatMode.Restart),
            label = "loaderPhase",
        )
        value
    }
    Row(
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Doodles.loaderPieces.forEachIndexed { index, doodle ->
            val hop = if (phase.toInt() == index) -(Sizes.LoaderHop.value) else 0f
            DoodleArt(
                doodle = doodle,
                modifier = Modifier
                    .size(Sizes.LoaderPiece)
                    .graphicsLayer { translationY = hop * density },
            )
        }
    }
}

/** Empty / error state: doodle + headline + body + optional action (Doodle Kit C4). */
@Composable
fun EmptyState(
    doodle: Doodle,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.M),
    ) {
        DoodleArt(doodle = doodle, modifier = Modifier.width(Sizes.EmptyArt))
        Text(text = title, style = PuzzleTheme.type.headline, textAlign = TextAlign.Center)
        if (body != null) Text(text = body, style = PuzzleTheme.type.body, textAlign = TextAlign.Center)
        action?.invoke()
    }
}
