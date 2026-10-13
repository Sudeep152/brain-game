package com.example.projectpuzzle.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.projectpuzzle.R
import com.example.projectpuzzle.ui.design.HintButton
import com.example.projectpuzzle.ui.design.IconPressableButton
import com.example.projectpuzzle.ui.design.InkCard
import com.example.projectpuzzle.ui.design.InkProgressBar
import com.example.projectpuzzle.ui.design.LabelText
import com.example.projectpuzzle.ui.design.PressableButton
import com.example.projectpuzzle.ui.design.ProjectPuzzleTheme
import com.example.projectpuzzle.ui.design.PuzzleButtonStyle
import com.example.projectpuzzle.ui.design.PuzzleColors
import com.example.projectpuzzle.ui.design.PuzzleIcons
import com.example.projectpuzzle.ui.design.PuzzleTheme
import com.example.projectpuzzle.ui.design.Radii
import com.example.projectpuzzle.ui.design.ScreenLayout
import com.example.projectpuzzle.ui.design.Sizes
import com.example.projectpuzzle.ui.design.Spacing
import com.example.projectpuzzle.ui.design.TextLink
import com.example.projectpuzzle.ui.design.TopBar
import com.example.projectpuzzle.ui.design.dottedPaper

/** Placeholder data until real progress/levels exist (T-304+). */
data class HomeUiState(
    val chapter: Int,
    val level: Int,
    val streakDays: Int,
    val solved: Int,
    val total: Int,
) {
    companion object {
        val Sample = HomeUiState(chapter = 1, level = 7, streakDays = 3, solved = 7, total = 40)
    }
}

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    state: HomeUiState = HomeUiState.Sample,
) {
    val type = PuzzleTheme.type
    ScreenColumn(scrollable = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.app_name), style = type.headline)
            IconPressableButton(
                icon = PuzzleIcons.Settings,
                contentDescription = stringResource(R.string.settings_button),
                onClick = onSettings,
                fill = PuzzleColors.Sky,
            )
        }

        InkCard(fill = PuzzleColors.Sun, modifier = Modifier.fillMaxWidth()) {
            LabelText(stringResource(R.string.chapter_label, state.chapter))
            Text(text = stringResource(R.string.level_label, state.level), style = type.display)
            Text(text = stringResource(R.string.home_sample_tagline), style = type.body)
            Spacer(Modifier.height(Spacing.XS))
            PressableButton(text = stringResource(R.string.play_button), onClick = onPlay, modifier = Modifier.fillMaxWidth())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.L),
        ) {
            InkCard(
                fill = PuzzleColors.Mint,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(Spacing.L),
                verticalArrangement = Arrangement.spacedBy(Spacing.XS),
            ) {
                LabelText(stringResource(R.string.daily_label))
                Text(text = stringResource(R.string.daily_sample_title), style = type.headline)
                TextLink(text = stringResource(R.string.daily_play), onClick = onPlay)
            }
            InkCard(
                fill = PuzzleColors.Sky,
                modifier = Modifier
                    .width(Sizes.StreakCardWidth)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(Spacing.M),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = state.streakDays.toString(), style = type.stat)
                Text(text = stringResource(R.string.streak_label), style = type.label, textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(Spacing.XXXL))

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(R.string.chapter_label, state.chapter), style = type.buttonSmall)
                Text(text = stringResource(R.string.progress_count, state.solved, state.total), style = type.buttonSmall)
            }
            InkProgressBar(
                progress = state.solved.toFloat() / state.total.coerceAtLeast(1),
                contentDescription = stringResource(R.string.cd_progress, state.solved, state.total),
            )
            TextLink(
                text = stringResource(R.string.all_levels),
                onClick = onPlay,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
fun PlayScreen(onBack: (() -> Unit)? = null) {
    val type = PuzzleTheme.type
    ScreenColumn(scrollable = false) {
        TopBar(
            title = stringResource(R.string.level_label, HomeUiState.Sample.level),
            backContentDescription = stringResource(R.string.cd_back),
            onBack = onBack,
            hint = {
                HintButton(
                    count = 3,
                    contentDescription = stringResource(R.string.cd_hint, 3),
                    onClick = {},
                )
            },
        )
        Text(
            text = stringResource(R.string.play_placeholder),
            style = type.title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        InkCard(
            fill = PuzzleColors.Surface,
            offRegister = false,
            radius = Radii.Board,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {}
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M), verticalAlignment = Alignment.CenterVertically) {
            PressableButton(
                text = stringResource(R.string.hint_button),
                onClick = {},
                style = PuzzleButtonStyle.Secondary,
                modifier = Modifier.weight(1f),
            )
            IconPressableButton(icon = PuzzleIcons.Restart, contentDescription = stringResource(R.string.cd_restart), onClick = {})
        }
        Text(
            text = stringResource(R.string.hint_ad_note),
            style = type.caption,
            color = PuzzleColors.InkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun SettingsScreen(onBack: (() -> Unit)? = null) {
    ScreenColumn(scrollable = true) {
        TopBar(
            title = stringResource(R.string.settings_title),
            backContentDescription = stringResource(R.string.cd_back),
            onBack = onBack,
        )
        InkCard(fill = PuzzleColors.Surface, offRegister = false, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.settings_placeholder), style = PuzzleTheme.type.body)
        }
    }
}

/** Dotted paper, system-bar insets and screen padding (design doc §5). */
@Composable
private fun ScreenColumn(scrollable: Boolean, content: @Composable ColumnScope.() -> Unit) {
    val base = Modifier
        .fillMaxSize()
        .dottedPaper()
        .systemBarsPadding()
    Column(
        modifier = (if (scrollable) base.verticalScroll(rememberScrollState()) else base)
            .padding(
                start = ScreenLayout.Horizontal,
                end = ScreenLayout.Horizontal,
                top = ScreenLayout.Top,
                bottom = ScreenLayout.Bottom,
            ),
        verticalArrangement = Arrangement.spacedBy(ScreenLayout.SectionGap),
        content = content,
    )
}

@Preview(name = "Home EN", widthDp = 390, heightDp = 844)
@Preview(name = "Home HI", widthDp = 390, heightDp = 844, locale = "hi")
@Composable
private fun HomePreview() {
    ProjectPuzzleTheme { HomeScreen(onPlay = {}, onSettings = {}) }
}

@Preview(name = "Play EN", widthDp = 390, heightDp = 844)
@Preview(name = "Play HI", widthDp = 390, heightDp = 844, locale = "hi")
@Composable
private fun PlayPreview() {
    ProjectPuzzleTheme { PlayScreen(onBack = {}) }
}
