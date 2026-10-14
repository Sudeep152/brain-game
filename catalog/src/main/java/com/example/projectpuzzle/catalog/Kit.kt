package com.example.projectpuzzle.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.projectpuzzle.designsystem.CaptionText
import com.example.projectpuzzle.designsystem.InkCard
import com.example.projectpuzzle.designsystem.LabelText
import com.example.projectpuzzle.designsystem.PuzzleColors
import com.example.projectpuzzle.designsystem.PuzzleTheme
import com.example.projectpuzzle.designsystem.Radii
import com.example.projectpuzzle.designsystem.ScreenLayout
import com.example.projectpuzzle.designsystem.Spacing
import com.example.projectpuzzle.designsystem.TopBar

/** Catalog preview language: true = Hindi sample text (the theme also switches line height / labels). */
val LocalCatalogHindi = staticCompositionLocalOf { false }

/** Pick sample text for the current preview language. */
@Composable
fun tr(en: String, hi: String): String = if (LocalCatalogHindi.current) hi else en

/** One catalog page: top bar + scrolling column of sections (screen padding 22 / 20 / 28, gap 28). */
@Composable
fun KitPage(
    title: String,
    kicker: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = ScreenLayout.Horizontal,
                end = ScreenLayout.Horizontal,
                top = ScreenLayout.Top,
                bottom = ScreenLayout.Bottom,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.XXXL - Spacing.XS),
    ) {
        TopBar(title = title, backContentDescription = "Back", onBack = onBack)
        LabelText(kicker)
        content()
    }
}

/** A titled group (canvas "grp"): headline, content, optional caption. */
@Composable
fun KitSection(
    title: String,
    caption: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
        Text(text = title, style = PuzzleTheme.type.headline)
        content()
        if (caption != null) CaptionText(caption)
    }
}

/** White flat board, radius 26 (canvas ".board"). */
@Composable
fun KitBoard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(Spacing.XL),
    content: @Composable ColumnScope.() -> Unit,
) {
    InkCard(
        fill = PuzzleColors.Surface,
        offRegister = false,
        radius = Radii.Board,
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(Spacing.L),
        modifier = modifier.fillMaxWidth(),
        content = content,
    )
}

/** Wrapping row of specimens. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KitFlow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.L),
        verticalArrangement = Arrangement.spacedBy(Spacing.L),
        content = content,
    )
}

/** One specimen with its caption under it (canvas ".spec"). */
@Composable
fun KitSpec(
    caption: String,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(Spacing.S),
    ) {
        content()
        CaptionText(caption, center = alignment == Alignment.CenterHorizontally)
    }
}
