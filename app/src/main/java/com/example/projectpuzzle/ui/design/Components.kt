package com.example.projectpuzzle.ui.design

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
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
    content: @Composable BoxScope.() -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
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

enum class PuzzleButtonStyle { Primary, Secondary }

/** Text button. Primary = red with white text; Secondary = white with ink text. Pass a width (e.g. fillMaxWidth). */
@Composable
fun PressableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PuzzleButtonStyle = PuzzleButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val type = PuzzleTheme.type
    val primary = style == PuzzleButtonStyle.Primary
    PressableSurface(
        onClick = onClick,
        fill = if (primary) PuzzleColors.Red else PuzzleColors.Surface,
        height = if (primary) Sizes.ButtonHeight else Sizes.ButtonSmallHeight,
        radius = Radii.Button,
        modifier = modifier,
        enabled = enabled,
    ) {
        Text(
            text = text,
            style = if (primary) type.button else type.buttonSmall,
            color = if (primary) PuzzleColors.OnRed else PuzzleColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.L),
        )
    }
}

/** 48 dp icon button with optional count badge. [contentDescription] is required for accessibility. */
@Composable
fun IconPressableButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fill: Color = PuzzleColors.Surface,
    badgeCount: Int? = null,
) {
    Box(modifier = modifier) {
        PressableSurface(
            onClick = onClick,
            fill = fill,
            height = Sizes.IconButton,
            radius = Radii.IconButton,
            modifier = Modifier
                .width(Sizes.IconButton)
                .semantics { this.contentDescription = contentDescription },
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.IconGlyph))
        }
        if (badgeCount != null) {
            CountBadge(
                count = badgeCount,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = Spacing.S, y = -Spacing.S),
            )
        }
    }
}

/** Small ink badge with a number (decorative: the parent's content description must include the count). */
@Composable
fun CountBadge(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(Sizes.Badge)
            .clip(CircleShape)
            .background(PuzzleColors.Ink)
            .border(Sizes.BadgeBorder, PuzzleColors.Surface, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = count.toString(), style = PuzzleTheme.type.label, color = PuzzleColors.Surface)
    }
}

/** Sun-colored hint button with remaining count. */
@Composable
fun HintButton(count: Int, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconPressableButton(
        icon = PuzzleIcons.Hint,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        fill = PuzzleColors.Sun,
        badgeCount = count,
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

/** Ink-outlined progress bar with red fill. */
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
                .background(PuzzleColors.Red),
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

/** Underlined text action with a 48 dp touch target. */
@Composable
fun TextLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .heightIn(min = Sizes.MinTouch)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text = text, style = PuzzleTheme.type.link)
    }
}
