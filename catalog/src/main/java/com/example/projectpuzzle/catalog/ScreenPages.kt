package com.example.projectpuzzle.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.projectpuzzle.designsystem.CaptionText
import com.example.projectpuzzle.designsystem.DoodleArt
import com.example.projectpuzzle.designsystem.DoodleLoader
import com.example.projectpuzzle.designsystem.Doodles
import com.example.projectpuzzle.designsystem.HintButton
import com.example.projectpuzzle.designsystem.HintSheet
import com.example.projectpuzzle.designsystem.IconPressableButton
import com.example.projectpuzzle.designsystem.InkCard
import com.example.projectpuzzle.designsystem.InkChip
import com.example.projectpuzzle.designsystem.InkProgressBar
import com.example.projectpuzzle.designsystem.LabelText
import com.example.projectpuzzle.designsystem.LevelTile
import com.example.projectpuzzle.designsystem.LevelTileState
import com.example.projectpuzzle.designsystem.PressableButton
import com.example.projectpuzzle.designsystem.PuzzleButtonStyle
import com.example.projectpuzzle.designsystem.PuzzleColors
import com.example.projectpuzzle.designsystem.PuzzleIcons
import com.example.projectpuzzle.designsystem.PuzzleTheme
import com.example.projectpuzzle.designsystem.Radii
import com.example.projectpuzzle.designsystem.RowDivider
import com.example.projectpuzzle.designsystem.ScreenLayout
import com.example.projectpuzzle.designsystem.Scrim
import com.example.projectpuzzle.designsystem.SegmentedControl
import com.example.projectpuzzle.designsystem.SettingGroup
import com.example.projectpuzzle.designsystem.SettingRow
import com.example.projectpuzzle.designsystem.Sizes
import com.example.projectpuzzle.designsystem.SolvedBurst
import com.example.projectpuzzle.designsystem.Spacing
import com.example.projectpuzzle.designsystem.StatCard
import com.example.projectpuzzle.designsystem.StatusBanner
import com.example.projectpuzzle.designsystem.TextLink
import com.example.projectpuzzle.designsystem.TopBar

/** Sample screens built only from :designsystem parts, one per Doodle Kit screen board (S1–S7). */
enum class SampleScreen(val title: String, val board: String) {
    Levels("Chapter levels", "S1"),
    Solved("Solved", "S2"),
    Hint("Hint sheet", "S3"),
    Settings("Settings", "S4"),
    Daily("Daily result & share card", "S5"),
    Welcome("First launch", "S6"),
    Offline("Loading & offline", "S7"),
}

@Composable
fun ScreensPage(onBack: () -> Unit, onOpen: (SampleScreen) -> Unit) {
    KitPage(title = "Full screens", kicker = "Doodle Kit · Screens", onBack = onBack) {
        CaptionText("Each screen is made only from design-system parts. Compare side by side with the canvas board. Back returns here.")
        InkCard(
            fill = PuzzleColors.Surface,
            offRegister = false,
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            SampleScreen.entries.forEachIndexed { index, screen ->
                if (index > 0) RowDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = Sizes.TextFieldHeight)
                        .clickable(role = Role.Button) { onOpen(screen) }
                        .padding(horizontal = Spacing.L + 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = screen.title, style = PuzzleTheme.type.body, modifier = Modifier.weight(1f))
                    Text(text = screen.board, style = PuzzleTheme.type.meta)
                }
            }
        }
    }
}

@Composable
fun SampleScreenHost(screen: SampleScreen, onBack: () -> Unit) {
    when (screen) {
        SampleScreen.Levels -> LevelsScreen(onBack)
        SampleScreen.Solved -> SolvedScreen(onBack)
        SampleScreen.Hint -> HintScreen(onBack)
        SampleScreen.Settings -> SettingsScreen(onBack)
        SampleScreen.Daily -> DailyScreen(onBack)
        SampleScreen.Welcome -> WelcomeScreen()
        SampleScreen.Offline -> OfflineScreen(onBack)
    }
}

/** Screen padding 22 / 20 / 28, gap 20; at least one screen tall so weight spacers push content down. */
@Composable
private fun ScreenFrame(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
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
}

// ---- S1 ----
@Composable
private fun LevelsScreen(onBack: () -> Unit) {
    ScreenFrame {
        TopBar(title = tr("Chapter 1", "अध्याय 1"), backContentDescription = "Back", onBack = onBack)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = tr("Solved", "हल हुईं"), style = PuzzleTheme.type.meta)
                Text(text = tr("6 of 40", "40 में से 6"), style = PuzzleTheme.type.meta)
            }
            InkProgressBar(progress = 6f / 40f, contentDescription = "6 of 40 solved")
        }
        Column(
            modifier = Modifier.padding(top = Spacing.XS),
            verticalArrangement = Arrangement.spacedBy(Spacing.XL),
        ) {
            (1..20).chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { n ->
                        val state = when {
                            n <= 6 -> LevelTileState.Solved
                            n == 7 -> LevelTileState.Current
                            else -> LevelTileState.Locked
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            LevelTile(number = n, state = state, contentDescription = "Level $n, ${state.name.lowercase()}", onClick = {})
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        InkCard(
            fill = PuzzleColors.Surface,
            offRegister = false,
            radius = Radii.CardSmall,
            contentPadding = PaddingValues(Spacing.L),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = PuzzleIcons.Lock, contentDescription = null, tint = PuzzleColors.Ink, modifier = Modifier.size(Sizes.RowIcon))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = tr("Chapter 2", "अध्याय 2"), style = PuzzleTheme.type.meta)
                    CaptionText(tr("Opens when you finish chapter 1", "अध्याय 1 पूरा करने पर खुलेगा"))
                }
            }
        }
    }
}

// ---- S2 ----
@Composable
private fun SolvedScreen(onBack: () -> Unit) {
    ScreenFrame {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconPressableButton(icon = PuzzleIcons.Home, contentDescription = "Home", onClick = onBack)
            Text(text = tr("Level 7", "लेवल 7"), style = PuzzleTheme.type.headline)
            Spacer(Modifier.width(Sizes.IconButton))
        }
        SolvedBurst(contentDescription = "Solved")
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.S), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = tr("Fire beats ice.", "आग ने बर्फ़ को हराया।"), style = PuzzleTheme.type.title, textAlign = TextAlign.Center)
            Text(text = tr("The leaf was just there to distract you.", "पत्ता तो बस ध्यान भटकाने के लिए था।"), style = PuzzleTheme.type.body, textAlign = TextAlign.Center)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp)) {
            StatCard(label = tr("Time", "समय"), value = "0:42", modifier = Modifier.weight(1f))
            StatCard(label = tr("Hints", "संकेत"), value = tr("None", "कोई नहीं"), modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.L)) {
            PressableButton(text = tr("Next level", "अगला लेवल"), onClick = {}, modifier = Modifier.fillMaxWidth())
            PressableButton(
                text = tr("Challenge a friend", "दोस्त को चुनौती दें"),
                onClick = {},
                style = PuzzleButtonStyle.Secondary,
                leadingIcon = PuzzleIcons.Share,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---- S3 ----
@Composable
private fun HintScreen(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = ScreenLayout.Horizontal, end = ScreenLayout.Horizontal, top = ScreenLayout.Top, bottom = ScreenLayout.Bottom),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.SectionGap),
        ) {
            TopBar(title = tr("Level 7", "लेवल 7"), backContentDescription = "Back", onBack = onBack, hint = {
                HintButton(count = 1, contentDescription = "Get a hint, 1 left", onClick = {})
            })
            Text(text = tr("Melt the ice.", "बर्फ़ पिघलाओ।"), style = PuzzleTheme.type.title, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            val board = RoundedCornerShape(Radii.Board)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(board)
                    .background(PuzzleColors.Surface)
                    .border(Sizes.OutlineUi, PuzzleColors.Ink, board),
            ) {
                // Ice block from the S3 board: outline 124 at (104, 68) → 100-unit doodle drawn 168 wide.
                DoodleArt(Doodles.block(PuzzleColors.Sky), Modifier.offset(x = 84.dp, y = 48.dp).width(168.dp))
            }
        }
        Scrim(onDismiss = onBack)
        HintSheet(
            title = tr("Hint 2 of 3", "संकेत 2 / 3"),
            used = 2,
            total = 3,
            stepsDescription = "2 of 3 hints used",
            hint = tr("What melts ice? Look at what you can move.", "बर्फ़ किससे पिघलती है? देखो क्या हिला सकते हो।"),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Text(
                text = tr("Still stuck? Hint 3 shows the answer.", "अब भी अटके? संकेत 3 जवाब दिखाता है।"),
                style = PuzzleTheme.type.meta,
                modifier = Modifier.padding(top = Spacing.XS),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.L), verticalAlignment = Alignment.CenterVertically) {
                PressableButton(text = tr("Watch video", "वीडियो देखें"), onClick = {}, leadingIcon = PuzzleIcons.PlayFilled, modifier = Modifier.weight(1f))
                TextLink(text = tr("Not now", "अभी नहीं"), onClick = onBack)
            }
            CaptionText(
                tr("Short video from our ad partner. Close it any time (no hint then).", "हमारे विज्ञापन पार्टनर का छोटा वीडियो। कभी भी बंद करें (तब संकेत नहीं मिलेगा)।"),
                center = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---- S4 ----
@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    var reminder by remember { mutableStateOf(false) }
    var language by remember { mutableIntStateOf(0) }
    ScreenFrame {
        TopBar(title = tr("Settings", "सेटिंग्स"), backContentDescription = "Back", onBack = onBack)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            LabelText(tr("Game", "गेम"))
            SettingGroup {
                SettingRow(label = tr("Sound", "आवाज़"), icon = PuzzleIcons.SoundOn, checked = sound, onCheckedChange = { sound = it })
                RowDivider()
                SettingRow(label = tr("Vibration", "कंपन"), icon = PuzzleIcons.Haptics, checked = vibration, onCheckedChange = { vibration = it })
                RowDivider()
                SettingRow(label = tr("Daily reminder", "रोज़ की याद"), icon = PuzzleIcons.Calendar, checked = reminder, onCheckedChange = { reminder = it })
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            LabelText(tr("Language", "भाषा"))
            SegmentedControl(
                options = listOf("English", "हिंदी"),
                selectedIndex = language,
                onSelect = { language = it },
                contentDescription = "Language",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            LabelText(tr("Help & privacy", "मदद और प्राइवेसी"))
            SettingGroup {
                SettingRow(label = tr("How to play", "कैसे खेलें"), icon = PuzzleIcons.Info, onClick = {})
                RowDivider()
                SettingRow(label = tr("Report a problem", "समस्या बताएँ"), icon = PuzzleIcons.Report, onClick = {})
                RowDivider()
                SettingRow(label = tr("Privacy choices", "प्राइवेसी विकल्प"), icon = PuzzleIcons.Privacy, onClick = {})
            }
        }
        Spacer(Modifier.weight(1f))
        CaptionText("[GAME NAME] · version 0.1.0", center = true, modifier = Modifier.fillMaxWidth())
    }
}

// ---- S5 ----
@Composable
private fun DailyScreen(onBack: () -> Unit) {
    ScreenFrame {
        TopBar(title = tr("Daily puzzle", "आज की पहेली"), backContentDescription = "Back", onBack = onBack)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp)) {
            StatCard(label = tr("Time", "समय"), value = "0:42", modifier = Modifier.weight(1f))
            StatCard(label = tr("Streak", "लगातार"), value = tr("3 days", "3 दिन"), modifier = Modifier.weight(1f))
        }
        Text(text = tr("Your share card", "आपका शेयर कार्ड"), style = PuzzleTheme.type.meta)
        InkCard(
            fill = PuzzleColors.Mint,
            contentPadding = PaddingValues(Spacing.XL),
            verticalArrangement = Arrangement.spacedBy(Spacing.M),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "[GAME NAME]", style = PuzzleTheme.type.headline)
                InkChip(text = "Oct 14")
            }
            Text(text = tr("Odd one out", "अलग कौन है"), style = PuzzleTheme.type.title)
            Column(
                modifier = Modifier.semantics { contentDescription = "Grid of 30 tiles, the odd one found at row 3" },
                verticalArrangement = Arrangement.spacedBy(Spacing.XS + 2.dp),
            ) {
                (0 until 30).chunked(6).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XS + 2.dp)) {
                        row.forEach { i ->
                            val cell = RoundedCornerShape(Radii.Highlight)
                            Box(
                                Modifier
                                    .size(Sizes.Badge)
                                    .clip(cell)
                                    .background(if (i == 15) PuzzleColors.Ink else PuzzleColors.Surface)
                                    .border(Sizes.BadgeBorder, PuzzleColors.Ink, cell),
                            )
                        }
                    }
                }
            }
            Text(text = tr("Found it in 0:42 · no hints", "0:42 में मिला · बिना संकेत"), style = PuzzleTheme.type.meta)
            Text(text = tr("Can you beat me?", "क्या आप मुझे हरा सकते हो?"), style = PuzzleTheme.type.headline)
        }
        Spacer(Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
            PressableButton(text = tr("Share", "शेयर करें"), onClick = {}, leadingIcon = PuzzleIcons.Share, modifier = Modifier.fillMaxWidth())
            CaptionText(tr("Shares this image and a Play Store link. No personal data.", "यह तस्वीर और प्ले स्टोर लिंक शेयर होगा। कोई निजी डेटा नहीं।"), center = true, modifier = Modifier.fillMaxWidth())
        }
    }
}

// ---- S6 ----
@Composable
private fun WelcomeScreen() {
    var language by remember { mutableIntStateOf(0) }
    ScreenFrame {
        Text(
            text = "[GAME NAME]",
            style = PuzzleTheme.type.headline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.M + 2.dp),
        )
        DoodleArt(Doodles.WelcomeHero, Modifier.fillMaxWidth())
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S + 2.dp)) {
            Text(text = tr("Think sideways.", "उल्टा सोचो।"), style = PuzzleTheme.type.display)
            Text(
                text = tr(
                    "Puzzles where the obvious answer is wrong. Tap, drag, shake, even flip your phone.",
                    "ऐसी पहेलियाँ जहाँ सीधा जवाब गलत है। टैप करो, खींचो, हिलाओ, फ़ोन उल्टा भी करो।",
                ),
                style = PuzzleTheme.type.body,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            LabelText(tr("Language", "भाषा"))
            SegmentedControl(
                options = listOf("English", "हिंदी"),
                selectedIndex = language,
                onSelect = { language = it },
                contentDescription = "Language",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
            PressableButton(text = tr("Let's play", "चलो खेलें"), onClick = {}, modifier = Modifier.fillMaxWidth())
            val bold = SpanStyle(color = PuzzleColors.Ink, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
            Text(
                text = buildAnnotatedString {
                    append("By playing you agree to the ")
                    withStyle(bold) { append("Terms") }
                    append(" and ")
                    withStyle(bold) { append("Privacy Policy") }
                    append(". Ad consent is asked where the law requires it.")
                },
                style = PuzzleTheme.type.caption,
                color = PuzzleColors.InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---- S7 ----
@Composable
private fun OfflineScreen(onBack: () -> Unit) {
    ScreenFrame {
        TopBar(title = tr("Hint 3", "संकेत 3"), backContentDescription = "Back", onBack = onBack)
        StatusBanner(icon = PuzzleIcons.WifiOff, text = tr("You're offline. Every puzzle still works.", "आप ऑफ़लाइन हैं। हर पहेली फिर भी चलेगी।"))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.L, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DoodleLoader(contentDescription = "Waiting for connection")
            Text(text = tr("The video needs internet", "वीडियो को इंटरनेट चाहिए"), style = PuzzleTheme.type.title, textAlign = TextAlign.Center)
            Text(
                text = tr(
                    "Hint videos load online. The game, levels and daily puzzle all work without it.",
                    "संकेत वीडियो ऑनलाइन लोड होते हैं। गेम, लेवल और आज की पहेली बिना इंटरनेट चलते हैं।",
                ),
                style = PuzzleTheme.type.body,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 290.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.L)) {
            PressableButton(text = tr("Try again", "फिर कोशिश करें"), onClick = {}, modifier = Modifier.fillMaxWidth())
            PressableButton(text = tr("Back to the puzzle", "पहेली पर लौटें"), onClick = onBack, style = PuzzleButtonStyle.Secondary, modifier = Modifier.fillMaxWidth())
            CaptionText(tr("While loading: doodles hop. After 5 s with no connection: this message.", "लोड होते समय: डूडल उछलते हैं। 5 सेकंड बिना कनेक्शन: यह संदेश।"), center = true, modifier = Modifier.fillMaxWidth())
        }
    }
}
