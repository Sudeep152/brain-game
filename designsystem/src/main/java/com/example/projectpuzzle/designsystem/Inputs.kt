package com.example.projectpuzzle.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset

/** 56×32 switch; on = mint (Doodle Kit C3). Put it in a [SettingRow] so the whole row is the touch target. */
@Composable
fun PuzzleSwitch(checked: Boolean, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val reduceMotion = LocalReduceMotion.current
    val knobTravel = Sizes.SwitchWidth - Sizes.SwitchKnob - (Sizes.SwitchInset + Sizes.OutlineUi) * 2
    val offset by animateDpAsState(
        targetValue = if (checked) knobTravel else 0.dp,
        animationSpec = if (reduceMotion) snap() else tween(Motion.PressMs * 2),
        label = "switch",
    )
    val shape = RoundedCornerShape(Sizes.SwitchHeight / 2)
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else DisabledAlpha)
            .size(width = Sizes.SwitchWidth, height = Sizes.SwitchHeight)
            .clip(shape)
            .background(if (checked) PuzzleColors.Mint else PuzzleColors.Surface)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape)
            .padding(Sizes.OutlineUi + Sizes.SwitchInset),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = offset)
                .size(Sizes.SwitchKnob)
                .clip(CircleShape)
                .background(PuzzleColors.Surface)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, CircleShape),
        )
    }
}

/** Icon · label · trailing control, 64 high; the whole row toggles or opens (Doodle Kit C3 / S4). */
@Composable
fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val interaction = when {
        checked != null && onCheckedChange != null ->
            Modifier.toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
        onClick != null -> Modifier.clickable(role = Role.Button, onClick = onClick)
        else -> Modifier
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.SettingRowHeight)
            .then(interaction)
            .padding(horizontal = Spacing.L + 2.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) Icon(imageVector = icon, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.RowIcon))
        Text(text = label, style = PuzzleTheme.type.body, modifier = Modifier.weight(1f))
        when {
            checked != null -> PuzzleSwitch(checked = checked)
            trailing != null -> trailing()
            onClick != null -> Icon(imageVector = PuzzleIcons.Chevron, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.ChevronGlyph))
        }
    }
}

/** White group card holding [SettingRow]s with hairline dividers (Doodle Kit S4). */
@Composable
fun SettingGroup(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    InkCard(
        fill = PuzzleColors.Surface,
        offRegister = false,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxWidth(),
    ) { content() }
}

/** Divider between rows in a [SettingGroup]. */
@Composable
fun RowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(Sizes.Divider)
            .background(PuzzleColors.PaperDot),
    )
}

/** 2–3 options, selected = sun, 52 high (Doodle Kit C3: language). */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Radii.IconButton)
    Row(
        modifier = modifier
            .height(Sizes.SegmentedHeight)
            .clip(shape)
            .background(PuzzleColors.Surface)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape)
            .semantics { this.contentDescription = contentDescription }
            .selectableGroup(),
    ) {
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Box(
                    Modifier
                        .width(Sizes.OutlineUi)
                        .fillMaxHeight()
                        .background(PuzzleColors.Ink),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (index == selectedIndex) PuzzleColors.Sun else PuzzleColors.Surface)
                    .selectable(selected = index == selectedIndex, role = Role.RadioButton, onClick = { onSelect(index) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = option, style = PuzzleTheme.type.segment, textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * Text field, 56 high, radius 16. Focused = sun off-register fill behind. Error = red icon + ink text
 * (never color alone) (Doodle Kit C3).
 */
@Composable
fun InkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val type = PuzzleTheme.type
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp)) {
        Text(text = label, style = type.meta)
        val shape = RoundedCornerShape(Radii.IconButton)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = type.body.copy(color = PuzzleColors.Ink),
            cursorBrush = SolidColor(PuzzleColors.Ink),
            interactionSource = interaction,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { this.contentDescription = label },
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Sizes.TextFieldHeight)
                        .drawBehind {
                            if (focused) {
                                val shift = Sizes.OffRegister.toPx()
                                drawRoundRect(
                                    color = PuzzleColors.Sun,
                                    topLeft = Offset(shift, shift),
                                    size = size,
                                    cornerRadius = CornerRadius(Radii.IconButton.toPx()),
                                )
                            }
                        }
                        .clip(shape)
                        .background(PuzzleColors.Surface)
                        .border(Sizes.OutlineUi, PuzzleColors.Ink, shape)
                        .padding(horizontal = Spacing.L),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(text = placeholder, style = type.body, color = PuzzleColors.InkSoft)
                    }
                    inner()
                }
            },
        )
        if (errorText != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XS + 2.dp), verticalAlignment = Alignment.CenterVertically) {
                ErrorDot()
                Text(text = errorText, style = type.meta)
            }
        }
    }
}

/** Red circle with a white "!" — the error marker used next to ink text. */
@Composable
fun ErrorDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(Sizes.ErrorDot)
            .clip(CircleShape)
            .background(PuzzleColors.Red),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "!", style = PuzzleTheme.type.label, color = PuzzleColors.Surface)
    }
}

/** Checkbox row; checked = mint + ink tick; row ≥ 48 (Doodle Kit C3). */
@Composable
fun InkCheckboxRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit, label: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(Sizes.CheckBoxRadius)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.MinTouch)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.spacedBy(Spacing.M),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Sizes.CheckBox)
                .clip(shape)
                .background(if (checked) PuzzleColors.Mint else PuzzleColors.Surface)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) Icon(imageVector = PuzzleIcons.Check, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.ChipGlyph))
        }
        Text(text = label, style = PuzzleTheme.type.body)
    }
}

/** Radio row; selected = ink dot (Doodle Kit C3). Wrap a group in Modifier.selectableGroup(). */
@Composable
fun InkRadioRow(selected: Boolean, onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.MinTouch)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Spacing.M),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Sizes.CheckBox)
                .clip(CircleShape)
                .background(PuzzleColors.Surface)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Box(Modifier.size(Sizes.RadioDot).clip(CircleShape).background(PuzzleColors.Ink))
        }
        Text(text = label, style = PuzzleTheme.type.body)
    }
}

enum class AnswerState { Normal, Selected, Correct, Wrong }

/** Quiz-style answer row: letter tile + text; Selected = sun, Correct = mint + tick, Wrong = dashed, pressed down (Doodle Kit C3). */
@Composable
fun AnswerOption(
    letter: String,
    text: String,
    state: AnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fill = when (state) {
        AnswerState.Selected -> PuzzleColors.Sun
        AnswerState.Correct -> PuzzleColors.Mint
        else -> PuzzleColors.Surface
    }
    if (state == AnswerState.Wrong) {
        val shape = RoundedCornerShape(Radii.Button)
        Row(
            modifier = modifier
                .fillMaxWidth()
                .offset(y = Sizes.PressEdge)
                .heightIn(min = Sizes.ButtonSmallHeight)
                .drawBehind {
                    val stroke = Sizes.OutlineUi.toPx()
                    drawRoundRect(
                        color = PuzzleColors.Ink,
                        topLeft = Offset(stroke / 2, stroke / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                        cornerRadius = CornerRadius(Radii.Button.toPx()),
                        style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))),
                    )
                }
                .clip(shape)
                .padding(horizontal = Spacing.L),
            horizontalArrangement = Arrangement.spacedBy(Spacing.M),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LetterTile(content = { Icon(imageVector = PuzzleIcons.Close, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.ChipGlyph)) })
            Text(text = text, style = PuzzleTheme.type.buttonSmall)
        }
        return
    }
    PressableSurface(
        onClick = onClick,
        fill = fill,
        height = Sizes.ButtonSmallHeight,
        radius = Radii.Button,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.L),
            horizontalArrangement = Arrangement.spacedBy(Spacing.M),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LetterTile(
                fill = if (state == AnswerState.Normal) Color.Transparent else PuzzleColors.Surface,
                content = {
                    if (state == AnswerState.Correct) {
                        Icon(imageVector = PuzzleIcons.Check, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.ChipGlyph))
                    } else {
                        Text(text = letter, style = PuzzleTheme.type.meta)
                    }
                },
            )
            Text(text = text, style = PuzzleTheme.type.buttonSmall)
        }
    }
}

@Composable
private fun LetterTile(fill: Color = Color.Transparent, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(Sizes.LetterTileRadius)
    Box(
        modifier = Modifier
            .size(Sizes.LetterTile)
            .clip(shape)
            .background(fill)
            .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
        contentAlignment = Alignment.Center,
    ) { content() }
}

/**
 * Code input: [length] boxes 58×66 with Kalam digits; the next empty box gets the sun off-register fill
 * (Doodle Kit C3). Opens the number keypad. [letterSlots] = smaller 44×52 slots for word answers
 * ("hidden-answer reveal", later puzzle family) — pass a text keyboard with it.
 */
@Composable
fun CodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    letterSlots: Boolean = false,
) {
    val type = PuzzleTheme.type
    val slotWidth = if (letterSlots) Sizes.LetterSlotWidth else Sizes.CodeBoxWidth
    val slotHeight = if (letterSlots) Sizes.LetterSlotHeight else Sizes.CodeBoxHeight
    val slotStyle: TextStyle = if (letterSlots) type.stamp else type.codeDigit
    BasicTextField(
        value = value,
        onValueChange = { new -> onValueChange(new.take(length)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        decorationBox = { inner ->
            Box {
                // The real text field is kept (invisible) so focus and the keyboard work; boxes draw the value.
                Box(Modifier.size(1.dp).alpha(0f)) { inner() }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M)) {
                    repeat(length) { index ->
                        val char = value.getOrNull(index)?.toString() ?: ""
                        val focusedBox = index == value.length
                        val shape = RoundedCornerShape(Radii.IconButton)
                        Box(
                            modifier = Modifier
                                .size(width = slotWidth, height = slotHeight)
                                .drawBehind {
                                    if (focusedBox) {
                                        val shift = Sizes.OffRegister.toPx()
                                        drawRoundRect(
                                            color = PuzzleColors.Sun,
                                            topLeft = Offset(shift, shift),
                                            size = size,
                                            cornerRadius = CornerRadius(Radii.IconButton.toPx()),
                                        )
                                    }
                                }
                                .clip(shape)
                                .background(PuzzleColors.Surface)
                                .border(Sizes.OutlineUi, PuzzleColors.Ink, shape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = char, style = slotStyle)
                        }
                    }
                }
            }
        },
    )
}

/**
 * Volume slider (Doodle Kit C3, optional — MVP uses switches): 14 dp track with red fill, 32 dp white thumb
 * on an ink edge. [value] is 0..1. Tap or drag anywhere on the 48 dp row.
 */
@Composable
fun InkSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val current = value.coerceIn(0f, 1f)
    val onChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableIntStateOf(0) }
    val thumbPx = with(LocalDensity.current) { Sizes.SliderThumb.toPx() }
    fun update(x: Float) {
        if (widthPx > 0) onChange((x / widthPx).coerceIn(0f, 1f))
    }
    val trackShape = RoundedCornerShape(Sizes.SliderTrack / 2)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Sizes.MinTouch)
            .onSizeChanged { widthPx = it.width }
            .semantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo(current = current, range = 0f..1f)
                setProgress { target ->
                    onChange(target.coerceIn(0f, 1f))
                    true
                }
            }
            .pointerInput(Unit) { detectTapGestures { offset -> update(offset.x) } }
            .pointerInput(Unit) { detectHorizontalDragGestures { change, _ -> update(change.position.x) } },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(Sizes.SliderTrack)
                .clip(trackShape)
                .background(PuzzleColors.Surface)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, trackShape),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(current)
                    .fillMaxHeight()
                    .background(PuzzleColors.Red),
            )
        }
        val thumbX = (current * widthPx - thumbPx / 2f).coerceIn(0f, (widthPx - thumbPx).coerceAtLeast(0f))
        Box(
            Modifier
                .offset { IntOffset(thumbX.toInt(), 0) }
                .size(Sizes.SliderThumb)
                .drawBehind {
                    drawCircle(PuzzleColors.Ink, radius = size.minDimension / 2f, center = Offset(size.width / 2f, size.height / 2f + Sizes.PressEdge.toPx()))
                }
                .clip(CircleShape)
                .background(PuzzleColors.Surface)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, CircleShape),
        )
    }
}
