package com.example.projectpuzzle.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projectpuzzle.designsystem.AnswerOption
import com.example.projectpuzzle.designsystem.AnswerState
import com.example.projectpuzzle.designsystem.CaptionText
import com.example.projectpuzzle.designsystem.CodeInput
import com.example.projectpuzzle.designsystem.CountBadge
import com.example.projectpuzzle.designsystem.DoodleLoader
import com.example.projectpuzzle.designsystem.Doodles
import com.example.projectpuzzle.designsystem.EmptyState
import com.example.projectpuzzle.designsystem.FeedbackBubble
import com.example.projectpuzzle.designsystem.FeedbackKind
import com.example.projectpuzzle.designsystem.HintButton
import com.example.projectpuzzle.designsystem.HintSheet
import com.example.projectpuzzle.designsystem.IconPressableButton
import com.example.projectpuzzle.designsystem.InkCard
import com.example.projectpuzzle.designsystem.InkCheckboxRow
import com.example.projectpuzzle.designsystem.InkChip
import com.example.projectpuzzle.designsystem.InkDialogCard
import com.example.projectpuzzle.designsystem.InkProgressBar
import com.example.projectpuzzle.designsystem.InkRadioRow
import com.example.projectpuzzle.designsystem.InkSlider
import com.example.projectpuzzle.designsystem.InkTextField
import com.example.projectpuzzle.designsystem.LabelText
import com.example.projectpuzzle.designsystem.LevelTile
import com.example.projectpuzzle.designsystem.LevelTileState
import com.example.projectpuzzle.designsystem.PressableButton
import com.example.projectpuzzle.designsystem.PuzzleButtonStyle
import com.example.projectpuzzle.designsystem.PuzzleColors
import com.example.projectpuzzle.designsystem.PuzzleIcons
import com.example.projectpuzzle.designsystem.PuzzleSwitch
import com.example.projectpuzzle.designsystem.PuzzleTheme
import com.example.projectpuzzle.designsystem.Radii
import com.example.projectpuzzle.designsystem.RewardedPrompt
import com.example.projectpuzzle.designsystem.RowDivider
import com.example.projectpuzzle.designsystem.Scrim
import com.example.projectpuzzle.designsystem.SegmentedControl
import com.example.projectpuzzle.designsystem.SettingGroup
import com.example.projectpuzzle.designsystem.SettingRow
import com.example.projectpuzzle.designsystem.Sizes
import com.example.projectpuzzle.designsystem.Spacing
import com.example.projectpuzzle.designsystem.StatusBanner
import com.example.projectpuzzle.designsystem.TextLink
import com.example.projectpuzzle.designsystem.TopBar

// ---------------------------------------------------------------------------------------------
// Buttons & links (canvas C1)
// ---------------------------------------------------------------------------------------------

@Composable
fun ButtonsPage(onBack: () -> Unit) {
    var busy by remember { mutableStateOf(true) }
    KitPage(title = "Buttons & links", kicker = "Doodle Kit · Components", onBack = onBack) {
        KitSection(title = "PrimaryButton · 60 high · red · one per screen") {
            val play = tr("Play", "खेलें")
            KitSpec("Normal") { PressableButton(text = play, onClick = {}, modifier = Modifier.fillMaxWidth()) }
            KitSpec("Pressed (face −5, edge hidden)") { PressableButton(text = play, onClick = {}, showPressed = true, modifier = Modifier.fillMaxWidth()) }
            KitSpec("Disabled · 45% · no edge motion") { PressableButton(text = play, onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) }
            KitSpec("Busy (e.g. video loading) · dots bounce · tap to toggle") {
                PressableButton(text = play, onClick = {}, busy = busy, modifier = Modifier.fillMaxWidth())
                TextLink(text = if (busy) "Stop busy" else "Make busy", onClick = { busy = !busy })
            }
            KitSpec("Hindi") { PressableButton(text = "अगला लेवल", onClick = {}, modifier = Modifier.fillMaxWidth()) }
        }
        KitSection(title = "SecondaryButton · 58 high · white") {
            val hint = tr("Stuck? Get a hint", "अटक गए? संकेत लें")
            KitSpec("Normal") { PressableButton(text = hint, onClick = {}, style = PuzzleButtonStyle.Secondary, modifier = Modifier.fillMaxWidth()) }
            KitSpec("Pressed") { PressableButton(text = hint, onClick = {}, style = PuzzleButtonStyle.Secondary, showPressed = true, modifier = Modifier.fillMaxWidth()) }
            KitSpec("Disabled") { PressableButton(text = hint, onClick = {}, style = PuzzleButtonStyle.Secondary, enabled = false, modifier = Modifier.fillMaxWidth()) }
            KitSpec("With leading icon") {
                PressableButton(
                    text = tr("Challenge a friend", "दोस्त को चुनौती दें"),
                    onClick = {},
                    style = PuzzleButtonStyle.Secondary,
                    leadingIcon = PuzzleIcons.Share,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            KitSpec("Hindi") { PressableButton(text = "अटक गए? संकेत लें", onClick = {}, style = PuzzleButtonStyle.Secondary, modifier = Modifier.fillMaxWidth()) }
        }
        KitSection(title = "IconButton · 48 (58 next to a 58 button) · label required") {
            KitFlow {
                KitSpec("surface") { IconPressableButton(icon = PuzzleIcons.Back, contentDescription = "Back", onClick = {}) }
                KitSpec("sky") { IconPressableButton(icon = PuzzleIcons.Settings, contentDescription = "Settings", onClick = {}, fill = PuzzleColors.Sky) }
                KitSpec("pressed") { IconPressableButton(icon = PuzzleIcons.Back, contentDescription = "Back, pressed", onClick = {}, showPressed = true) }
                KitSpec("disabled") { IconPressableButton(icon = PuzzleIcons.Share, contentDescription = "Share, disabled", onClick = {}, enabled = false) }
                KitSpec("large 58") { IconPressableButton(icon = PuzzleIcons.Restart, contentDescription = "Restart level", onClick = {}, size = Sizes.ButtonSmallHeight) }
            }
        }
        KitSection(title = "HintButton · sun · count badge") {
            KitFlow {
                KitSpec("Hints left") { HintButton(count = 3, contentDescription = "Get a hint, 3 left", onClick = {}) }
                KitSpec("Next hint = video") { HintButton(count = 0, videoNext = true, contentDescription = "Get a hint by watching a short video", onClick = {}) }
                KitSpec("All 3 used") { HintButton(count = 0, contentDescription = "All hints used", onClick = {}) }
            }
        }
        KitSection(title = "TextLink · 16 bold underline · 48 touch height") {
            KitFlow {
                KitSpec("Normal") { TextLink(text = tr("All levels", "सभी लेवल"), onClick = {}) }
                KitSpec("Pressed · sun highlight") { TextLink(text = tr("All levels", "सभी लेवल"), onClick = {}, showPressed = true) }
                KitSpec("Hindi") { TextLink(text = "सभी लेवल", onClick = {}) }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Cards, bars, status (canvas C2)
// ---------------------------------------------------------------------------------------------

@Composable
fun CardsPage(onBack: () -> Unit) {
    KitPage(title = "Cards & bars", kicker = "Doodle Kit · Components", onBack = onBack) {
        KitSection(title = "InkCard · off-register (26) · small (20) · flat surface") {
            KitSpec("HeroCard · sun · 24 / 20 / 24 · gap 12", alignment = Alignment.Start) {
                InkCard(
                    fill = PuzzleColors.Sun,
                    contentPadding = PaddingValues(start = Spacing.XL, end = Spacing.XL, top = Spacing.XXL, bottom = Spacing.XXL),
                    verticalArrangement = Arrangement.spacedBy(Spacing.M),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = tr("Chapter 1", "अध्याय 1"), style = PuzzleTheme.type.meta)
                    Text(text = tr("Level 7", "लेवल 7"), style = PuzzleTheme.type.display)
                    Text(text = tr("Fire, ice and one clever move.", "आग, बर्फ़ और एक चालाक चाल।"), style = PuzzleTheme.type.body)
                    PressableButton(text = tr("Play", "खेलें"), onClick = {}, modifier = Modifier.fillMaxWidth())
                }
            }
            KitSpec("InfoCard · mint · 16 · gap 4", alignment = Alignment.Start) {
                InkCard(
                    fill = PuzzleColors.Mint,
                    radius = Radii.CardSmall,
                    contentPadding = PaddingValues(Spacing.L),
                    verticalArrangement = Arrangement.spacedBy(Spacing.XS),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LabelText(tr("Daily puzzle", "आज की पहेली"))
                    Text(text = tr("Odd one out", "अलग कौन है"), style = PuzzleTheme.type.headline)
                    TextLink(text = tr("Play today's", "आज की खेलें"), onClick = {})
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.L)) {
                KitSpec("StatCard · 116 wide") {
                    InkCard(
                        fill = PuzzleColors.Sky,
                        radius = Radii.CardSmall,
                        contentPadding = PaddingValues(Spacing.L),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(Sizes.StreakCardWidth),
                    ) {
                        Text(text = "3", style = PuzzleTheme.type.stat)
                        Text(text = tr("day streak", "दिन लगातार"), style = PuzzleTheme.type.meta)
                    }
                }
                KitSpec("SurfaceCard · flat white · 26", modifier = Modifier.weight(1f)) {
                    InkCard(
                        fill = PuzzleColors.Surface,
                        offRegister = false,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CaptionText("Puzzle board / settings group", center = true)
                    }
                }
            }
        }
        KitSection(title = "TopBar · back · title (headline) · trailing slot") {
            KitSpec("Puzzle: back · level · hint", alignment = Alignment.Start) {
                TopBar(title = tr("Level 7", "लेवल 7"), backContentDescription = "Back", onBack = {}, hint = {
                    HintButton(count = 3, contentDescription = "Get a hint, 3 left", onClick = {})
                })
            }
            KitSpec("Daily: back · title · timer chip", alignment = Alignment.Start) {
                TopBar(title = tr("Daily puzzle", "आज की पहेली"), backContentDescription = "Back", onBack = {}, hint = {
                    InkChip(text = "0:42", fill = PuzzleColors.Mint, leadingIcon = PuzzleIcons.Timer)
                })
            }
            KitSpec("Plain: back · title · empty 48 slot (title stays centred)", alignment = Alignment.Start) {
                TopBar(title = tr("Settings", "सेटिंग्स"), backContentDescription = "Back", onBack = {})
            }
            KitSpec("Home: wordmark left · settings right", alignment = Alignment.Start) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "[GAME NAME]", style = PuzzleTheme.type.headline)
                    IconPressableButton(icon = PuzzleIcons.Settings, contentDescription = "Settings", onClick = {}, fill = PuzzleColors.Sky)
                }
            }
        }
        KitSection(title = "ProgressBar · 20 high", caption = "Complete chapter turns mint") {
            listOf(0, 7, 40).forEach { solved ->
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = tr("Chapter 1", "अध्याय 1"), style = PuzzleTheme.type.meta)
                        Text(text = tr("$solved of 40", "40 में से $solved"), style = PuzzleTheme.type.meta)
                    }
                    InkProgressBar(progress = solved / 40f, contentDescription = "$solved of 40")
                }
            }
        }
        KitSection(
            title = "Chips & badges · radius 11 · 34 high",
            caption = "Chips show facts (never tappable). Badge: ink, white 2 border, count ≤ 9.",
        ) {
            KitFlow {
                InkChip(text = tr("Chapter 1", "अध्याय 1"))
                InkChip(text = "Daily · Oct 14", fill = PuzzleColors.Mint)
                InkChip(text = tr("New", "नया"), fill = PuzzleColors.Sun)
                InkChip(text = "0:42", leadingIcon = PuzzleIcons.Timer)
                InkChip(text = tr("2 hints", "2 संकेत"), leadingIcon = PuzzleIcons.Hint)
                Box(Modifier.width(Sizes.Badge)) { CountBadge(count = 3) }
                Box(Modifier.width(Sizes.Badge)) { CountBadge(count = 12) }
            }
        }
        KitSection(title = "LevelTile · 72 · four states") {
            KitFlow {
                KitSpec("Solved") { LevelTile(number = 6, state = LevelTileState.Solved, contentDescription = "Level 6, solved", onClick = {}) }
                KitSpec("Current") { LevelTile(number = 7, state = LevelTileState.Current, contentDescription = "Level 7, current", onClick = {}) }
                KitSpec("Unlocked") { LevelTile(number = 8, state = LevelTileState.Unlocked, contentDescription = "Level 8, open", onClick = {}) }
                KitSpec("Locked · dashed") { LevelTile(number = 9, state = LevelTileState.Locked, contentDescription = "Level 9, locked", onClick = {}) }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Inputs & controls (canvas C3)
// ---------------------------------------------------------------------------------------------

@Composable
fun InputsPage(onBack: () -> Unit) {
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(false) }
    var language by remember { mutableIntStateOf(0) }
    var volume by remember { mutableFloatStateOf(0.6f) }
    var name by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("twelve") }
    var reminder by remember { mutableStateOf(true) }
    var usage by remember { mutableStateOf(false) }
    var reason by remember { mutableIntStateOf(0) }
    var option by remember { mutableIntStateOf(1) }
    var code by remember { mutableStateOf("42") }
    var letters by remember { mutableStateOf("CA") }

    KitPage(title = "Inputs & controls", kicker = "Doodle Kit · Components", onBack = onBack) {
        KitSection(title = "Switch · 56 × 32 (touch row ≥ 48)") {
            KitFlow {
                KitSpec("On · mint") { PuzzleSwitch(checked = true) }
                KitSpec("Off · white") { PuzzleSwitch(checked = false) }
                KitSpec("Disabled") { PuzzleSwitch(checked = true, enabled = false) }
            }
            SettingGroup {
                SettingRow(label = tr("Sound", "आवाज़"), icon = PuzzleIcons.SoundOn, checked = sound, onCheckedChange = { sound = it })
                RowDivider()
                SettingRow(label = tr("Vibration", "कंपन"), icon = PuzzleIcons.Haptics, checked = vibration, onCheckedChange = { vibration = it })
            }
            CaptionText("SettingRow · icon · label · control · 64 high · whole row tappable")
        }
        KitSection(title = "SegmentedControl · language", caption = "Selected = sun. 52 high. Max 3 options.") {
            SegmentedControl(
                options = listOf("English", "हिंदी"),
                selectedIndex = language,
                onSelect = { language = it },
                contentDescription = "Language",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        KitSection(title = "Slider · volume (optional)", caption = "Only if we add separate music / effects volume. MVP uses switches.") {
            InkSlider(value = volume, onValueChange = { volume = it }, contentDescription = "Volume")
        }
        KitSection(title = "TextField · 56 high · radius 16", caption = "For: typed-answer puzzles, feedback form, later a player name. Tap a field to see the focused sun glow.") {
            KitSpec("Empty · placeholder inkSoft", alignment = Alignment.Start) {
                InkTextField(value = name, onValueChange = { name = it }, label = tr("Your name", "आपका नाम"), placeholder = tr("Type here", "यहाँ लिखें"), modifier = Modifier.fillMaxWidth())
            }
            KitSpec("Error · red icon + ink text (red text is too faint), never color alone", alignment = Alignment.Start) {
                InkTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = tr("Answer", "जवाब"),
                    errorText = tr("Use numbers, like 12", "अंकों में लिखें, जैसे 12"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        KitSection(title = "Checkbox & radio · 26 · row ≥ 48", caption = "Checked checkbox = mint + ink tick. Radio dot = ink.") {
            InkCheckboxRow(checked = reminder, onCheckedChange = { reminder = it }, label = tr("Send me the daily puzzle reminder", "रोज़ की पहेली की याद दिलाएँ"))
            InkCheckboxRow(checked = usage, onCheckedChange = { usage = it }, label = tr("Share anonymous usage data", "गुमनाम उपयोग डेटा शेयर करें"))
            InkRadioRow(selected = reason == 0, onClick = { reason = 0 }, label = tr("The puzzle is too hard", "पहेली बहुत कठिन है"))
            InkRadioRow(selected = reason == 1, onClick = { reason = 1 }, label = tr("Something looks broken", "कुछ टूटा लग रहा है"))
        }
        KitSection(title = "AnswerOption · quiz-style choices", caption = "Tap A or B to select. Correct and Wrong are fixed examples.") {
            AnswerOption(letter = "A", text = tr("A towel", "एक तौलिया"), state = if (option == 0) AnswerState.Selected else AnswerState.Normal, onClick = { option = 0 })
            AnswerOption(letter = "B", text = tr("A sponge", "एक स्पंज"), state = if (option == 1) AnswerState.Selected else AnswerState.Normal, onClick = { option = 1 })
            AnswerOption(letter = "C", text = tr("Correct", "सही"), state = AnswerState.Correct, onClick = {})
            AnswerOption(letter = "D", text = tr("Wrong · shakes, then fades", "गलत · हिलता है, फिर फीका"), state = AnswerState.Wrong, onClick = {})
        }
        KitSection(title = "CodeInput · lock / number answers", caption = "58 × 66 boxes · Kalam digits · next box gets the sun off-register fill. Opens the number keypad.") {
            CodeInput(value = code, onValueChange = { code = it }, length = 4, contentDescription = "4 digit code")
        }
        KitSection(title = "Hidden-answer reveal", caption = "Letter slots for word answers (later puzzle family).") {
            CodeInput(
                value = letters,
                onValueChange = { letters = it.uppercase() },
                length = 3,
                contentDescription = "3 letter word",
                keyboardType = KeyboardType.Text,
                letterSlots = true,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Feedback & overlays (canvas C4)
// ---------------------------------------------------------------------------------------------

@Composable
fun FeedbackPage(onBack: () -> Unit) {
    var overlay by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        KitPage(title = "Feedback & overlays", kicker = "Doodle Kit · Components", onBack = onBack) {
            KitSection(title = "FeedbackBubble · pops near the tap, 1.2 s, never blocks") {
                KitFlow {
                    KitSpec("nope · + shake + light haptic") { FeedbackBubble(kind = FeedbackKind.Nope, text = tr("Not quite!", "नहीं!")) }
                    KitSpec("close · right idea, wrong spot") { FeedbackBubble(kind = FeedbackKind.Close, text = tr("So close!", "बस थोड़ा सा!")) }
                    KitSpec("yes · step done (multi-step levels)") { FeedbackBubble(kind = FeedbackKind.Yes, text = tr("Yes!", "हाँ!")) }
                    KitSpec("Hindi") { FeedbackBubble(kind = FeedbackKind.Nope, text = "नहीं!") }
                }
            }
            KitSection(
                title = "BottomSheet · hint",
                caption = "White sheet · 26 top corners · ink handle · slides up 200 ms over a 40% ink scrim",
            ) {
                HintSheet(
                    title = tr("Hint 2 of 3", "संकेत 2 / 3"),
                    used = 2,
                    total = 3,
                    stepsDescription = "2 of 3 hints used",
                    hint = tr("What melts ice? Look at what you can move.", "बर्फ़ किससे पिघलती है? देखो क्या हिला सकते हो।"),
                ) {
                    PressableButton(text = tr("Show the answer", "जवाब दिखाओ"), onClick = {}, leadingIcon = PuzzleIcons.PlayFilled, modifier = Modifier.fillMaxWidth())
                    CaptionText(tr("Hint 3 shows the solution · unlocks after a short video", "संकेत 3 हल दिखाता है · छोटा वीडियो देखकर खुलता है"), center = true, modifier = Modifier.fillMaxWidth())
                }
                PressableButton(text = "Open over a scrim", onClick = { overlay = true }, style = PuzzleButtonStyle.Secondary, modifier = Modifier.fillMaxWidth())
            }
            KitSection(title = "Dialog · confirm", caption = "Max 2 actions. Destructive action never red-filled by default; red stays for \"Play / Next\".") {
                InkDialogCard(
                    title = tr("Restart this level?", "लेवल फिर से शुरू करें?"),
                    body = tr("Objects go back to the start. Hints you used stay used.", "चीज़ें शुरू की जगह लौट जाएँगी। इस्तेमाल हुए संकेत वापस नहीं मिलेंगे।"),
                ) {
                    PressableButton(text = tr("Cancel", "रद्द करें"), onClick = {}, style = PuzzleButtonStyle.Secondary, modifier = Modifier.weight(1f))
                    PressableButton(text = tr("Restart", "फिर से"), onClick = {}, style = PuzzleButtonStyle.Secondary, fill = PuzzleColors.Sun, modifier = Modifier.weight(1f))
                }
            }
            KitSection(title = "RewardedPrompt · before any video", caption = "Always ask first. Never auto-play. \"Not now\" is always visible.") {
                RewardedPrompt(
                    title = tr("Watch a short video?", "छोटा वीडियो देखें?"),
                    body = tr("You get the next hint.", "आपको अगला संकेत मिलेगा।"),
                    watchText = tr("Watch", "देखें"),
                    onWatch = {},
                    notNowText = tr("Not now", "अभी नहीं"),
                    onNotNow = {},
                )
            }
            KitSection(title = "Status: banner, loading, empty, error") {
                KitSpec("Banner · top of screen · auto-hides", alignment = Alignment.Start) {
                    StatusBanner(icon = PuzzleIcons.WifiOff, text = tr("Offline. Puzzles still work.", "ऑफ़लाइन। पहेलियाँ फिर भी चलेंगी।"))
                }
                KitSpec("Loader · 3 doodles hop in turn") { DoodleLoader(contentDescription = "Loading") }
                KitSpec("Empty state · doodle + headline + one line") {
                    EmptyState(doodle = Doodles.EmptyCalendar, title = tr("New puzzle at midnight", "नई पहेली आधी रात को"))
                }
                KitSpec("Error · doodle + headline + retry") {
                    EmptyState(doodle = Doodles.ErrorMark, title = tr("That didn't load", "यह लोड नहीं हुआ"), action = {
                        PressableButton(text = tr("Try again", "फिर कोशिश करें"), onClick = {}, style = PuzzleButtonStyle.Secondary)
                    })
                }
            }
        }
        if (overlay) {
            Scrim(onDismiss = { overlay = false })
            HintSheet(
                title = tr("Hint 2 of 3", "संकेत 2 / 3"),
                used = 2,
                total = 3,
                stepsDescription = "2 of 3 hints used",
                hint = tr("What melts ice? Look at what you can move.", "बर्फ़ किससे पिघलती है? देखो क्या हिला सकते हो।"),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                PressableButton(text = tr("Close", "बंद करें"), onClick = { overlay = false }, style = PuzzleButtonStyle.Secondary, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
