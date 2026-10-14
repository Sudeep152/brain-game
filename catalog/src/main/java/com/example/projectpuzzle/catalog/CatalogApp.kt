package com.example.projectpuzzle.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.projectpuzzle.designsystem.CaptionText
import com.example.projectpuzzle.designsystem.InkCard
import com.example.projectpuzzle.designsystem.InkChip
import com.example.projectpuzzle.designsystem.LabelText
import com.example.projectpuzzle.designsystem.ProjectPuzzleTheme
import com.example.projectpuzzle.designsystem.PuzzleColors
import com.example.projectpuzzle.designsystem.PuzzleTheme
import com.example.projectpuzzle.designsystem.RowDivider
import com.example.projectpuzzle.designsystem.ScreenLayout
import com.example.projectpuzzle.designsystem.SegmentedControl
import com.example.projectpuzzle.designsystem.Sizes
import com.example.projectpuzzle.designsystem.Spacing
import com.example.projectpuzzle.designsystem.dottedPaper

/** Catalog pages, grouped like the Doodle Kit canvas. [count] = items shown on the page. */
enum class KitPageId(val title: String, val group: String, val dot: Color, val count: Int) {
    Colors("Colors", "Foundations", PuzzleColors.Red, 11),
    Type("Type scale", "Foundations", PuzzleColors.Sun, 18),
    Space("Space, shape, icons, motion", "Foundations", PuzzleColors.Mint, 4),
    Buttons("Buttons & links", "Components", PuzzleColors.Red, 5),
    Cards("Cards, bars, status", "Components", PuzzleColors.Sun, 5),
    Inputs("Inputs & controls", "Components", PuzzleColors.Sky, 7),
    Feedback("Feedback & overlays", "Components", PuzzleColors.Mint, 7),
    Objects("Puzzle objects & states", "Game pieces & screens", PuzzleColors.Sky, 21),
    Device("Device & sensor cues", "Game pieces & screens", PuzzleColors.Red, 12),
    Celebrate("Celebration & rewards", "Game pieces & screens", PuzzleColors.Mint, 6),
    Screens("Full screens", "Game pieces & screens", PuzzleColors.Sun, SampleScreen.entries.size),
}

@Composable
fun CatalogApp() {
    var hindi by rememberSaveable { mutableStateOf(false) }
    var largeText by rememberSaveable { mutableStateOf(false) }
    var motion by rememberSaveable { mutableStateOf(true) }
    var pageName by rememberSaveable { mutableStateOf<String?>(null) }
    var screenName by rememberSaveable { mutableStateOf<String?>(null) }
    val page = pageName?.let { KitPageId.valueOf(it) }
    val screen = screenName?.let { SampleScreen.valueOf(it) }

    BackHandler(enabled = page != null) {
        if (screenName != null) screenName = null else pageName = null
    }

    ProjectPuzzleTheme(
        languageOverride = if (hindi) "hi" else "en",
        fontScaleOverride = if (largeText) 1.3f else 1f,
        reduceMotionOverride = !motion,
    ) {
        CompositionLocalProvider(LocalCatalogHindi provides hindi) {
            Box(
                Modifier
                    .fillMaxSize()
                    .dottedPaper()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                val back = { pageName = null }
                when (page) {
                    null -> CatalogHome(
                        hindi = hindi,
                        onHindi = { hindi = it },
                        largeText = largeText,
                        onLargeText = { largeText = it },
                        motion = motion,
                        onMotion = { motion = it },
                        onOpen = { pageName = it.name },
                    )
                    KitPageId.Colors -> ColorsPage(back)
                    KitPageId.Type -> TypePage(back)
                    KitPageId.Space -> SpacePage(back)
                    KitPageId.Buttons -> ButtonsPage(back)
                    KitPageId.Cards -> CardsPage(back)
                    KitPageId.Inputs -> InputsPage(back)
                    KitPageId.Feedback -> FeedbackPage(back)
                    KitPageId.Objects -> ObjectsPage(back)
                    KitPageId.Device -> DevicePage(back)
                    KitPageId.Celebrate -> CelebratePage(back)
                    KitPageId.Screens -> if (screen == null) {
                        ScreensPage(onBack = back, onOpen = { screenName = it.name })
                    } else {
                        SampleScreenHost(screen = screen, onBack = { screenName = null })
                    }
                }
            }
        }
    }
}

/** Catalog home (Doodle Kit S8). */
@Composable
private fun CatalogHome(
    hindi: Boolean,
    onHindi: (Boolean) -> Unit,
    largeText: Boolean,
    onLargeText: (Boolean) -> Unit,
    motion: Boolean,
    onMotion: (Boolean) -> Unit,
    onOpen: (KitPageId) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(ScreenLayout.SectionGap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Doodle Kit", style = PuzzleTheme.type.title)
                CaptionText("Marker Doodle design system · v1")
            }
            InkChip(text = "Debug", fill = PuzzleColors.Sky)
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            LabelText("Preview with")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp)) {
                SegmentedControl(
                    options = listOf("EN", "हिं"),
                    selectedIndex = if (hindi) 1 else 0,
                    onSelect = { onHindi(it == 1) },
                    contentDescription = "Language",
                    modifier = Modifier.weight(1f),
                )
                SegmentedControl(
                    options = listOf("1.0×", "1.3×"),
                    selectedIndex = if (largeText) 1 else 0,
                    onSelect = { onLargeText(it == 1) },
                    contentDescription = "Font size",
                    modifier = Modifier.weight(1f),
                )
                SegmentedControl(
                    options = listOf("Motion", "Off"),
                    selectedIndex = if (motion) 0 else 1,
                    onSelect = { onMotion(it == 0) },
                    contentDescription = "Motion",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        KitPageId.entries.groupBy { it.group }.forEach { (group, pages) ->
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
                LabelText(group)
                InkCard(
                    fill = PuzzleColors.Surface,
                    offRegister = false,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    pages.forEachIndexed { index, page ->
                        if (index > 0) RowDivider()
                        CatalogRow(page = page, onClick = { onOpen(page) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogRow(page: KitPageId, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.TextFieldHeight)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.L + 2.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(Sizes.StepDot)
                .clip(CircleShape)
                .background(page.dot)
                .border(Sizes.OutlineUi, PuzzleColors.Ink, CircleShape),
        )
        Text(text = page.title, style = PuzzleTheme.type.body, modifier = Modifier.weight(1f))
        Text(text = page.count.toString(), style = PuzzleTheme.type.meta)
    }
}
