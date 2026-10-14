package com.example.projectpuzzle.designsystem

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------------------------
// Shared motion
// ---------------------------------------------------------------------------------------------

/**
 * "Nope" shake: ±6 dp × 3 in 240 ms each time [active] turns true (design doc §7). Read `.value` inside
 * `graphicsLayer { translationX = shake.value * Motion.NopeShake.toPx() }`. Still when animations are off.
 */
@Composable
internal fun rememberNopeShake(active: Boolean): Animatable<Float, AnimationVector1D> {
    val reduceMotion = LocalReduceMotion.current
    val shake = remember { Animatable(0f) }
    LaunchedEffect(active, reduceMotion) {
        shake.snapTo(0f)
        if (active && !reduceMotion) {
            val step = Motion.NopeMs / 6
            shake.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = Motion.NopeMs
                    -1f at step
                    1f at step * 2
                    -1f at step * 3
                    1f at step * 4
                    -1f at step * 5
                },
            )
        }
    }
    return shake
}

// ---------------------------------------------------------------------------------------------
// Puzzle objects (Doodle Kit G1)
// ---------------------------------------------------------------------------------------------

/** Interaction states shared by every puzzle object (Doodle Kit G1). */
enum class ObjectState { Idle, Pressed, Dragging, DropTarget, Correct, Wrong, HintHighlight }

/**
 * A puzzle object in one of its [ObjectState]s. Frame is 100 × 110 scene units (the extra 10 on top leaves
 * room for lift/shake); the [doodle] fills the width. Size it with a width modifier.
 *
 * Idle · Pressed (squash 1.05/0.92) · Dragging (1.08, −6°, shadow) · DropTarget (sun dashed ring) ·
 * Correct (mint + tick) · Wrong (red outline, motion marks, shake) · HintHighlight (soft sun pulse).
 */
@Composable
fun PuzzleObject(doodle: Doodle, state: ObjectState, modifier: Modifier = Modifier) {
    val reduceMotion = LocalReduceMotion.current
    val shake = rememberNopeShake(state == ObjectState.Wrong)
    val hintAlpha = if (state == ObjectState.HintHighlight && !reduceMotion) {
        val pulse = rememberInfiniteTransition(label = "hintPulse")
        val a by pulse.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.45f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 700), RepeatMode.Reverse),
            label = "hintAlpha",
        )
        a
    } else {
        0.45f
    }
    val spec: AnimationSpec<Float> = if (reduceMotion) snap() else spring(dampingRatio = Motion.PressDampingRatio)
    val scaleX by animateFloatAsState(
        targetValue = when (state) {
            ObjectState.Pressed -> 1.05f
            ObjectState.Dragging -> Motion.DragLiftScale
            ObjectState.DropTarget -> 0.84f
            else -> 1f
        },
        animationSpec = spec,
        label = "objScaleX",
    )
    val scaleY by animateFloatAsState(
        targetValue = when (state) {
            ObjectState.Pressed -> 0.92f
            ObjectState.Dragging -> Motion.DragLiftScale
            ObjectState.DropTarget -> 0.84f
            else -> 1f
        },
        animationSpec = spec,
        label = "objScaleY",
    )
    val rotation by animateFloatAsState(
        targetValue = if (state == ObjectState.Dragging) Motion.DragLiftRotationDeg else 0f,
        animationSpec = spec,
        label = "objRotation",
    )
    val lift by animateFloatAsState(
        targetValue = if (state == ObjectState.Dragging) 6f else 0f,
        animationSpec = spec,
        label = "objLift",
    )
    val origin = when (state) {
        ObjectState.Pressed -> TransformOrigin(0.5f, 0.52f)
        ObjectState.Dragging -> TransformOrigin(0.5f, 0.46f)
        ObjectState.DropTarget -> TransformOrigin(0.24f, 0.24f)
        else -> TransformOrigin.Center
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(100f / 110f)
            .graphicsLayer { translationX = shake.value * Motion.NopeShake.toPx() },
    ) {
        val unit = maxWidth / 100f
        Canvas(Modifier.matchParentSize()) {
            val k = size.width / 100f
            when (state) {
                ObjectState.HintHighlight ->
                    drawCircle(PuzzleColors.Sun.copy(alpha = hintAlpha), radius = 47f * k, center = Offset(49f * k, 59f * k))
                ObjectState.DropTarget -> drawRoundRect(
                    color = PuzzleColors.Sun,
                    topLeft = Offset(4f * k, 14f * k),
                    size = Size(90f * k, 90f * k),
                    cornerRadius = CornerRadius(20f * k),
                    style = Stroke(width = 4f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f * k, 7f * k))),
                )
                ObjectState.Dragging -> drawOval(
                    color = PuzzleColors.Ink.copy(alpha = 0.25f),
                    topLeft = Offset(22f * k, 96f * k),
                    size = Size(60f * k, 12f * k),
                )
                ObjectState.Wrong -> {
                    val marks = listOf(
                        Offset(2f, 40f) to Offset(4f, 50f), Offset(2f, 64f) to Offset(4f, 74f),
                        Offset(96f, 40f) to Offset(94f, 50f), Offset(96f, 64f) to Offset(94f, 74f),
                    )
                    marks.forEach { (a, b) -> drawLine(PuzzleColors.Ink, a * k, b * k, strokeWidth = 3f * k, cap = StrokeCap.Round) }
                }
                else -> Unit
            }
        }
        DoodleArt(
            doodle = doodle,
            bodyOverride = if (state == ObjectState.Correct) PuzzleColors.Mint else null,
            outlineOverride = if (state == ObjectState.Wrong) PuzzleColors.Red else null,
            outlineWidthOverride = if (state == ObjectState.Wrong) 4f else null,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = unit * 10f)
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                    rotationZ = rotation
                    translationY = -lift * unit.toPx()
                    transformOrigin = origin
                },
        )
        if (state == ObjectState.Correct) {
            Canvas(Modifier.matchParentSize()) {
                val k = size.width / 100f
                val tick = Path().apply {
                    moveTo(30f * k, 60f * k)
                    lineTo(42f * k, 72f * k)
                    lineTo(66f * k, 46f * k)
                }
                drawPath(tick, PuzzleColors.Ink, style = Stroke(width = 4f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Matchstick, tokens, odd-one-out (Doodle Kit G1)
// ---------------------------------------------------------------------------------------------

enum class MatchstickState { Idle, Lifted, Slot }

/**
 * Matchstick (60 × 110 units, vertical; rotate with the modifier for horizontal sticks). Wood #E8B66A is the
 * only non-token color and is matchstick-only (D-021). Slot = empty place, red dashes.
 */
@Composable
fun Matchstick(state: MatchstickState, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = Sizes.MatchstickWidth, height = Sizes.MatchstickHeight)) {
        scale(scale = size.width / 60f, pivot = Offset.Zero) {
            when (state) {
                MatchstickState.Idle -> drawStick(top = 12f, bottom = 96f)
                MatchstickState.Lifted -> {
                    drawOval(PuzzleColors.Ink.copy(alpha = 0.25f), topLeft = Offset(16f, 100f), size = Size(36f, 8f))
                    rotate(degrees = 10f, pivot = Offset(30f, 54f)) { drawStick(top = 8f, bottom = 92f) }
                }
                MatchstickState.Slot -> drawLine(
                    color = PuzzleColors.Red,
                    start = Offset(30f, 12f),
                    end = Offset(30f, 96f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f)),
                )
            }
        }
    }
}

private fun DrawScope.drawStick(top: Float, bottom: Float) {
    drawLine(PuzzleColors.Ink, Offset(30f, top), Offset(30f, bottom), strokeWidth = 12f, cap = StrokeCap.Round)
    drawLine(PuzzleColors.Wood, Offset(30f, top), Offset(30f, bottom), strokeWidth = 7f, cap = StrokeCap.Round)
    drawCircle(PuzzleColors.Red, radius = 7f, center = Offset(30f, bottom))
    drawCircle(PuzzleColors.Ink, radius = 7f, center = Offset(30f, bottom), style = Stroke(width = 2.5f))
}

/** Tappable number: 84 dp ink circle, Kalam digits (Doodle Kit G1). */
@Composable
fun NumberToken(text: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Box(
        modifier = modifier
            .size(Sizes.NumberToken)
            .clip(CircleShape)
            .background(PuzzleColors.Surface)
            .border(Sizes.OutlineObject, PuzzleColors.Ink, CircleShape)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = PuzzleTheme.type.tokenNumber)
    }
}

/** Draggable word: sun fill, dashed 3 dp ink border = "can be picked up" (Doodle Kit G1). */
@Composable
fun WordToken(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(PuzzleColors.Sun, RoundedCornerShape(Radii.Chip))
            .drawBehind {
                val stroke = Sizes.OutlineObject.toPx()
                drawRoundRect(
                    color = PuzzleColors.Ink,
                    topLeft = Offset(stroke / 2f, stroke / 2f),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(Radii.Chip.toPx()),
                    style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(9.dp.toPx(), 6.dp.toPx()))),
                )
            }
            .padding(horizontal = Spacing.M + 2.dp, vertical = Spacing.XS + 2.dp),
    ) {
        Text(text = text, style = PuzzleTheme.type.tokenWord)
    }
}

enum class OddTileState { Normal, Found, Wrong }

/** Odd-one-out tile, 64 dp: normal · found (mint, art tilts 14°) · wrong pick (red outline + shake). */
@Composable
fun OddOneTile(
    state: OddTileState,
    contentDescription: String,
    modifier: Modifier = Modifier,
    art: Doodle = Doodles.TileStar,
    artRotation: Float = 0f,
    onClick: (() -> Unit)? = null,
) {
    val shake = rememberNopeShake(state == OddTileState.Wrong)
    val shape = RoundedCornerShape(Radii.IconButton)
    Box(
        modifier = modifier
            .graphicsLayer { translationX = shake.value * Motion.NopeShake.toPx() }
            .size(Sizes.OddTile)
            .semantics { this.contentDescription = contentDescription }
            .clip(shape)
            .background(if (state == OddTileState.Found) PuzzleColors.Mint else PuzzleColors.Surface)
            .border(Sizes.OutlineUi, if (state == OddTileState.Wrong) PuzzleColors.Red else PuzzleColors.Ink, shape)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        DoodleArt(
            doodle = art,
            modifier = Modifier
                .width(Sizes.OddTileArt)
                .graphicsLayer { rotationZ = if (state == OddTileState.Found) 14f else artRotation },
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Device, sensor & media (Doodle Kit G2)
// ---------------------------------------------------------------------------------------------

/** 120 dp white tile with a device-trick doodle and its name. Used inside hints and the tutorial. */
@Composable
fun DeviceCueTile(doodle: Doodle, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(Sizes.DeviceCueTile),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.S),
    ) {
        val shape = RoundedCornerShape(Radii.CardSmall)
        Box(
            modifier = Modifier
                .size(Sizes.DeviceCueTile)
                .clip(shape)
                .background(PuzzleColors.Surface)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
            contentAlignment = Alignment.Center,
        ) {
            DoodleArt(doodle = doodle, modifier = Modifier.width(Sizes.DeviceCueArt))
        }
        Text(text = label, style = PuzzleTheme.type.meta, textAlign = TextAlign.Center)
    }
}

/** "Never stuck" card: tap fallback for a device trick (shown after hint 3 or when the sensor is missing). */
@Composable
fun SensorFallbackCard(
    title: String,
    body: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InkCard(
        fill = PuzzleColors.Sun,
        radius = Radii.CardSmall,
        contentPadding = PaddingValues(Spacing.L),
        verticalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp),
        modifier = modifier,
    ) {
        Text(text = title, style = PuzzleTheme.type.meta)
        Text(text = body, style = PuzzleTheme.type.body)
        PressableButton(text = actionText, onClick = onAction, style = PuzzleButtonStyle.Secondary, modifier = Modifier.fillMaxWidth())
    }
}

/**
 * LATER · not in MVP. Explains a camera/mic permission before the Android dialog; says what is NOT collected
 * and always offers Skip. Needs owner approval + privacy policy update before use (Doodle Kit G2).
 */
@Composable
fun PermissionPrimer(
    icon: ImageVector,
    iconFill: Color,
    title: String,
    body: String,
    allowText: String,
    onAllow: () -> Unit,
    skipText: String,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InkCard(
        fill = PuzzleColors.Surface,
        contentPadding = PaddingValues(horizontal = Spacing.XL, vertical = Spacing.XXL),
        verticalArrangement = Arrangement.spacedBy(Spacing.M),
        modifier = modifier,
    ) {
        val shape = RoundedCornerShape(Radii.CardSmall)
        Box(
            modifier = Modifier
                .size(Sizes.PrimerIcon)
                .clip(shape)
                .background(iconFill)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.PrimerGlyph))
        }
        Text(text = title, style = PuzzleTheme.type.title)
        Text(text = body, style = PuzzleTheme.type.body)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M), verticalAlignment = Alignment.CenterVertically) {
            PressableButton(text = allowText, onClick = onAllow, modifier = Modifier.weight(1f))
            TextLink(text = skipText, onClick = onSkip)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Celebration & rewards (Doodle Kit G3 / S2)
// ---------------------------------------------------------------------------------------------

private val SolvedTick = Doodle(24f, 24f, listOf(DoodleLayer("M5 12.5l4.5 4.5L19 7.5", LayerRole.Line, PuzzleColors.Ink, 2.8f)))

/** 160 dp mint off-register circle with an ink tick. */
@Composable
fun SolvedBadge(contentDescription: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(Sizes.SolvedBadge)
            .semantics { this.contentDescription = contentDescription }
            .drawBehind {
                val radius = size.minDimension / 2f
                val shift = Sizes.OffRegister.toPx()
                drawCircle(PuzzleColors.Mint, radius = radius, center = Offset(radius + shift, radius + shift))
                val stroke = Sizes.OutlineObject.toPx()
                drawCircle(PuzzleColors.Ink, radius = radius - stroke / 2f, center = Offset(radius, radius), style = Stroke(width = stroke))
            },
        contentAlignment = Alignment.Center,
    ) {
        DoodleArt(doodle = SolvedTick, modifier = Modifier.width(Sizes.SolvedGlyph))
    }
}

/**
 * Solved moment: badge pops 1 → 1.15 → 1, confetti doodles fly out (600 ms), success haptic.
 * With animations off everything is shown at rest. Re-run by changing the composable's key.
 */
@Composable
fun SolvedBurst(contentDescription: String, modifier: Modifier = Modifier) {
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current
    val pop = remember { Animatable(1f) }
    val burst = remember { Animatable(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(Unit) {
        val haptic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.LONG_PRESS
        view.performHapticFeedback(haptic)
        if (!reduceMotion) {
            launch {
                pop.animateTo(
                    targetValue = 1f,
                    animationSpec = keyframes {
                        durationMillis = Motion.SolvedMs
                        Motion.SolvedPopScale at Motion.SolvedMs / 3
                    },
                )
            }
            burst.animateTo(1f, tween(durationMillis = Motion.SolvedMs))
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Sizes.BurstHeight),
        contentAlignment = Alignment.Center,
    ) {
        DoodleArt(
            doodle = Doodles.BurstConfetti,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = burst.value
                    val s = 0.4f + 0.6f * burst.value
                    scaleX = s
                    scaleY = s
                },
        )
        SolvedBadge(
            contentDescription = contentDescription,
            modifier = Modifier.graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
            },
        )
    }
}

/** Chapter complete: sun card with a tilted red "DONE!" stamp (Doodle Kit G3). */
@Composable
fun ChapterCompleteCard(
    stampText: String,
    title: String,
    body: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    InkCard(
        fill = PuzzleColors.Sun,
        contentPadding = PaddingValues(horizontal = Spacing.XL, vertical = Spacing.XXL),
        verticalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(Sizes.Stamp)
                .semantics { this.contentDescription = contentDescription }
                .graphicsLayer { rotationZ = -10f }
                .clip(CircleShape)
                .background(PuzzleColors.Surface)
                .border(Sizes.StampBorder, PuzzleColors.Red, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stampText, style = PuzzleTheme.type.stamp)
        }
        Text(text = title, style = PuzzleTheme.type.title, textAlign = TextAlign.Center)
        Text(text = body, style = PuzzleTheme.type.body, textAlign = TextAlign.Center)
    }
}

/** Streak milestone: sky card with an off-register star (Doodle Kit G3). */
@Composable
fun StreakCard(title: String, body: String, modifier: Modifier = Modifier) {
    InkCard(
        fill = PuzzleColors.Sky,
        contentPadding = PaddingValues(horizontal = Spacing.XL, vertical = Spacing.XXL),
        verticalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        DoodleArt(doodle = Doodles.StreakStar, modifier = Modifier.width(Sizes.StreakStar))
        Text(text = title, style = PuzzleTheme.type.title, textAlign = TextAlign.Center)
        Text(text = body, style = PuzzleTheme.type.body, textAlign = TextAlign.Center)
    }
}

/** LATER · not in MVP (idea from direction E). Round sticker with a white rim on an ink edge, tilted 6°. */
@Composable
fun StickerBadge(art: Doodle, contentDescription: String, modifier: Modifier = Modifier, fill: Color = PuzzleColors.Red) {
    Box(
        modifier = modifier
            .size(Sizes.Sticker)
            .semantics { this.contentDescription = contentDescription }
            .graphicsLayer { rotationZ = 6f }
            .drawBehind {
                val radius = size.minDimension / 2f
                val ring = Sizes.OutlineUi.toPx()
                val rim = Sizes.StickerBorder.toPx()
                val c = Offset(radius, radius)
                drawCircle(PuzzleColors.Ink, radius = radius, center = c + Offset(0f, Sizes.PressEdge.toPx()))
                drawCircle(PuzzleColors.Ink, radius = radius, center = c)
                drawCircle(PuzzleColors.Surface, radius = radius - ring, center = c)
                drawCircle(fill, radius = radius - ring - rim, center = c)
            },
        contentAlignment = Alignment.Center,
    ) {
        DoodleArt(doodle = art, modifier = Modifier.width(Sizes.StickerArt))
    }
}
