package com.example.projectpuzzle.ui.design

import android.provider.Settings
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

val LocalPuzzleTypography = staticCompositionLocalOf { DefaultPuzzleTypography }

/** True when the system "remove animations" setting is on (animator scale = 0). */
val LocalReduceMotion = staticCompositionLocalOf { false }

/** Latin labels are uppercased; Devanagari has no case, so Hindi labels are not. */
val LocalUppercaseLabels = staticCompositionLocalOf { true }

object PuzzleTheme {
    val type: PuzzleTypography
        @Composable @ReadOnlyComposable
        get() = LocalPuzzleTypography.current
}

private val DevanagariLanguages = setOf("hi", "mr", "ne")

@Composable
fun ProjectPuzzleTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val language = LocalConfiguration.current.locales.get(0).language
    val devanagari = language in DevanagariLanguages
    val reduceMotion = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val typography = if (devanagari) DefaultPuzzleTypography.withLineHeightScale(1.15f) else DefaultPuzzleTypography

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PuzzleColors.Red,
            onPrimary = PuzzleColors.OnRed,
            secondary = PuzzleColors.Sun,
            onSecondary = PuzzleColors.Ink,
            tertiary = PuzzleColors.Mint,
            onTertiary = PuzzleColors.Ink,
            background = PuzzleColors.Paper,
            onBackground = PuzzleColors.Ink,
            surface = PuzzleColors.Surface,
            onSurface = PuzzleColors.Ink,
            outline = PuzzleColors.Ink,
        ),
    ) {
        CompositionLocalProvider(
            LocalPuzzleTypography provides typography,
            LocalReduceMotion provides reduceMotion,
            LocalUppercaseLabels provides !devanagari,
            LocalContentColor provides PuzzleColors.Ink,
            LocalTextStyle provides typography.body,
            content = content,
        )
    }
}
