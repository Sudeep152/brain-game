package com.example.projectpuzzle.designsystem

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/** Dotted paper ground — design doc §2.4. Apply to the root of every screen. */
fun Modifier.dottedPaper(): Modifier = drawBehind {
    drawRect(PuzzleColors.Paper)
    val step = Sizes.DotGrid.toPx()
    val radius = Sizes.DotRadius.toPx()
    var y = step / 2f
    while (y < size.height) {
        var x = step / 2f
        while (x < size.width) {
            drawCircle(PuzzleColors.PaperDot, radius, Offset(x, y))
            x += step
        }
        y += step
    }
}

/**
 * Ink outline card. With [offRegister] the [fill] is printed 6 dp right + 6 dp down from the outline
 * (signature look, design doc §2.1); without it the fill sits exactly inside the outline (boards, surfaces).
 */
@Composable
fun InkCard(
    fill: Color,
    modifier: Modifier = Modifier,
    offRegister: Boolean = true,
    radius: Dp = Radii.Card,
    contentPadding: PaddingValues = PaddingValues(Spacing.XL),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Spacing.S),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .drawBehind {
                val corner = CornerRadius(radius.toPx())
                val shift = if (offRegister) Sizes.OffRegister.toPx() else 0f
                drawRoundRect(color = fill, topLeft = Offset(shift, shift), size = size, cornerRadius = corner)
                val stroke = Sizes.OutlineUi.toPx()
                drawRoundRect(
                    color = PuzzleColors.Ink,
                    topLeft = Offset(stroke / 2f, stroke / 2f),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius((radius.toPx() - stroke / 2f).coerceAtLeast(0f)),
                    style = Stroke(width = stroke),
                )
            }
            .padding(contentPadding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

/**
 * Base for every tappable surface: ink "edge" under the face; pressing moves the face down onto the edge
 * (design doc §2.3, §7). Light haptic on click. Respects reduce-motion.
 */
@Composable
fun PressableSurface(
    onClick: () -> Unit,
    fill: Color,
    height: Dp,
    radius: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showPressed: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val touched by source.collectIsPressedAsState()
    // showPressed: freeze the pressed look for the Doodle Kit catalog and previews.
    val pressed = (touched || showPressed) && enabled
    val reduceMotion = LocalReduceMotion.current
    val edge = Sizes.PressEdge
    val spec: AnimationSpec<Dp> = if (reduceMotion) {
        snap()
    } else {
        spring(dampingRatio = Motion.PressDampingRatio, stiffness = Spring.StiffnessMedium)
    }
    val shift by animateDpAsState(targetValue = if (pressed) edge else 0.dp, animationSpec = spec, label = "press")
    val view = LocalView.current
    val shape = RoundedCornerShape(radius)

    Box(
        modifier = modifier
            .height(height + edge)
            .alpha(if (enabled) 1f else DisabledAlpha)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onClick()
            },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = edge)
                .background(PuzzleColors.Ink, shape),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset { IntOffset(0, shift.roundToPx()) }
                .background(fill, shape)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/** Disabled controls: 45% opacity, no press motion (Doodle Kit C1). */
const val DisabledAlpha = 0.45f

enum class PuzzleButtonStyle { Primary, Secondary }

/** Text button. Primary = red with white text; Secondary = white with ink text. Pass a width (e.g. fillMaxWidth). */
@Composable
fun PressableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PuzzleButtonStyle = PuzzleButtonStyle.Primary,
    enabled: Boolean = true,
    busy: Boolean = false,
    leadingIcon: ImageVector? = null,
    fill: Color? = null,
    showPressed: Boolean = false,
) {
    val type = PuzzleTheme.type
    val primary = style == PuzzleButtonStyle.Primary
    val contentColor = if (primary) PuzzleColors.OnRed else PuzzleColors.Ink
    PressableSurface(
        onClick = onClick,
        showPressed = showPressed,
        // fill: secondary actions may use sun (e.g. "Restart" in a confirm dialog, Doodle Kit C4).
        fill = fill ?: if (primary) PuzzleColors.Red else PuzzleColors.Surface,
        height = if (primary) Sizes.ButtonHeight else Sizes.ButtonSmallHeight,
        radius = Radii.Button,
        modifier = modifier.semantics { if (busy) stateDescription = text },
        enabled = enabled && !busy,
    ) {
        if (busy) {
            BusyDots(color = contentColor)
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.S),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Spacing.L),
            ) {
                if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = contentColor, modifier = Modifier.size(Sizes.IconGlyph))
                }
                Text(
                    text = text,
                    style = if (primary) type.button else type.buttonSmall,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Three dots that pulse in turn while a button is busy (static when animations are off). */
@Composable
fun BusyDots(color: Color, modifier: Modifier = Modifier) {
    val reduceMotion = LocalReduceMotion.current
    val phase = if (reduceMotion) {
        0f
    } else {
        val transition = rememberInfiniteTransition(label = "busy")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 900), RepeatMode.Restart),
            label = "busyPhase",
        )
        value
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.S)) {
        repeat(3) { index ->
            val active = phase.toInt() == index
            Box(
                Modifier
                    .size(Sizes.BusyDot)
                    .alpha(if (active || reduceMotion) 1f else 0.4f)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

/** Square icon button (default 48 dp; use [Sizes.ButtonSmallHeight] next to a secondary button) with optional count badge. */
@Composable
fun IconPressableButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fill: Color = PuzzleColors.Surface,
    badgeCount: Int? = null,
    size: Dp = Sizes.IconButton,
    enabled: Boolean = true,
    showPressed: Boolean = false,
    badge: (@Composable (Modifier) -> Unit)? = null,
) {
    Box(modifier = modifier) {
        PressableSurface(
            onClick = onClick,
            fill = fill,
            height = size,
            radius = Radii.IconButton,
            enabled = enabled,
            showPressed = showPressed,
            modifier = Modifier
                .width(size)
                .semantics { this.contentDescription = contentDescription },
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.IconGlyph))
        }
        val badgeModifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = Spacing.S, y = -Spacing.S)
        when {
            badge != null -> badge(badgeModifier)
            badgeCount != null -> CountBadge(count = badgeCount, modifier = badgeModifier)
        }
    }
}

/** Small ink badge (decorative: the parent's content description must say what it means). */
@Composable
fun Badge(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .requiredSize(Sizes.Badge)
            .clip(CircleShape)
            .background(PuzzleColors.Ink)
            .border(Sizes.BadgeBorder, PuzzleColors.Surface, CircleShape),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Badge with a number (max 9 shown as "9+"). */
@Composable
fun CountBadge(count: Int, modifier: Modifier = Modifier) {
    Badge(modifier) {
        Text(text = if (count > 9) "9+" else count.toString(), style = PuzzleTheme.type.label, color = PuzzleColors.Surface)
    }
}

/** Badge with a small play triangle: "next one is a video". */
@Composable
fun VideoBadge(modifier: Modifier = Modifier) {
    Badge(modifier) {
        Icon(imageVector = PuzzleIcons.Play, contentDescription = null, tint = PuzzleColors.Surface, modifier = Modifier.size(Sizes.BadgeGlyph))
    }
}

/**
 * Sun hint button (Doodle Kit C1). [count] free hints left shows a number; when [videoNext] the badge
 * shows a play triangle (next hint needs a video); with nothing left the button is disabled.
 */
@Composable
fun HintButton(
    count: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    videoNext: Boolean = false,
) {
    val enabled = count > 0 || videoNext
    IconPressableButton(
        icon = PuzzleIcons.Hint,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        fill = PuzzleColors.Sun,
        enabled = enabled,
        badge = when {
            videoNext -> { m -> VideoBadge(m) }
            count > 0 -> { m -> CountBadge(count, m) }
            else -> null
        },
    )
}

/** Back · title · hint. Missing back/hint keep their 48 dp slot so the title stays centered. */
@Composable
fun TopBar(
    title: String,
    backContentDescription: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    hint: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconPressableButton(icon = PuzzleIcons.Back, contentDescription = backContentDescription, onClick = onBack)
        } else {
            Spacer(Modifier.width(Sizes.IconButton))
        }
        Text(text = title, style = PuzzleTheme.type.headline, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        if (hint != null) hint() else Spacer(Modifier.width(Sizes.IconButton))
    }
}

/** Ink-outlined progress bar with red fill; a complete bar turns mint (Doodle Kit C2). */
@Composable
fun InkProgressBar(progress: Float, contentDescription: String, modifier: Modifier = Modifier) {
    val value = progress.coerceIn(0f, 1f)
    val shape = RoundedCornerShape(Sizes.ProgressHeight / 2)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Sizes.ProgressHeight)
            .semantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo(current = value, range = 0f..1f)
            }
            .clip(shape)
            .background(PuzzleColors.Surface)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
    ) {
        Box(
            Modifier
                .fillMaxWidth(value)
                .fillMaxHeight()
                .background(if (value >= 1f) PuzzleColors.Mint else PuzzleColors.Red),
        )
    }
}

/** Small label; uppercased for Latin scripts only (design doc §4). */
@Composable
fun LabelText(text: String, modifier: Modifier = Modifier, color: Color = PuzzleColors.Ink) {
    Text(
        text = if (LocalUppercaseLabels.current) text.uppercase() else text,
        style = PuzzleTheme.type.label,
        color = color,
        modifier = modifier,
    )
}

/** Underlined text action with a 48 dp touch target; pressed = sun highlight (Doodle Kit C1). */
@Composable
fun TextLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, showPressed: Boolean = false) {
    val source = remember { MutableInteractionSource() }
    val touched by source.collectIsPressedAsState()
    val pressed = touched || showPressed
    Box(
        modifier = modifier
            .heightIn(min = Sizes.MinTouch)
            .background(if (pressed) PuzzleColors.Sun else Color.Transparent, RoundedCornerShape(Radii.Highlight))
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.XS),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text = text, style = PuzzleTheme.type.link)
    }
}
