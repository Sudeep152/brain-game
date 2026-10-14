package com.example.projectpuzzle.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projectpuzzle.designsystem.CaptionText
import com.example.projectpuzzle.designsystem.ChapterCompleteCard
import com.example.projectpuzzle.designsystem.PuzzleTheme
import androidx.compose.material3.Text
import com.example.projectpuzzle.designsystem.DeviceCueTile
import com.example.projectpuzzle.designsystem.DoodleArt
import com.example.projectpuzzle.designsystem.Doodles
import com.example.projectpuzzle.designsystem.InkCard
import com.example.projectpuzzle.designsystem.InkChip
import com.example.projectpuzzle.designsystem.Matchstick
import com.example.projectpuzzle.designsystem.MatchstickState
import com.example.projectpuzzle.designsystem.NumberToken
import com.example.projectpuzzle.designsystem.ObjectState
import com.example.projectpuzzle.designsystem.OddOneTile
import com.example.projectpuzzle.designsystem.OddTileState
import com.example.projectpuzzle.designsystem.PermissionPrimer
import com.example.projectpuzzle.designsystem.PuzzleColors
import com.example.projectpuzzle.designsystem.PuzzleIcons
import com.example.projectpuzzle.designsystem.PuzzleObject
import com.example.projectpuzzle.designsystem.Radii
import com.example.projectpuzzle.designsystem.SensorFallbackCard
import com.example.projectpuzzle.designsystem.Sizes
import com.example.projectpuzzle.designsystem.SolvedBurst
import com.example.projectpuzzle.designsystem.Spacing
import com.example.projectpuzzle.designsystem.StatCard
import com.example.projectpuzzle.designsystem.StatusBanner
import com.example.projectpuzzle.designsystem.StickerBadge
import com.example.projectpuzzle.designsystem.StreakCard
import com.example.projectpuzzle.designsystem.TextLink
import com.example.projectpuzzle.designsystem.WordToken

// ---------------------------------------------------------------------------------------------
// Puzzle objects & states (canvas G1)
// ---------------------------------------------------------------------------------------------

@Composable
fun ObjectsPage(onBack: () -> Unit) {
    var replay by remember { mutableIntStateOf(0) }
    var oddWrong by remember { mutableIntStateOf(0) }
    KitPage(title = "Puzzle objects", kicker = "Doodle Kit · Game pieces", onBack = onBack) {
        CaptionText(
            "Every object = 3 dp ink outline + fill shifted 6 right / 6 down. Drawn in code from simple shapes, " +
                "so they scale and stay consistent. Min tap size 124 scene units (~48 dp).",
        )
        KitSection(title = "Shape primitives") {
            KitBoard {
                KitFlow {
                    listOf(
                        "circle" to Doodles.circle(PuzzleColors.Red),
                        "rounded rect" to Doodles.roundRect(PuzzleColors.Sun),
                        "triangle" to Doodles.triangle(PuzzleColors.Mint),
                        "star" to Doodles.star(PuzzleColors.Sun),
                        "blob" to Doodles.blob(PuzzleColors.Sky),
                        "line / path" to Doodles.Squiggle,
                    ).forEach { (name, doodle) ->
                        KitSpec(name) { DoodleArt(doodle, Modifier.width(Sizes.LevelTile)) }
                    }
                }
            }
        }
        KitSection(title = "Object library (MVP set, grows per level)") {
            KitBoard {
                KitFlow {
                    Doodles.objects.forEach { (name, doodle) ->
                        KitSpec(name) { DoodleArt(doodle, Modifier.width(Sizes.LevelTile)) }
                    }
                }
            }
        }
        KitSection(title = "Interaction states (same rules for every object)") {
            KitBoard {
                KitFlow {
                    listOf(
                        ObjectState.Idle to "Idle",
                        ObjectState.Pressed to "Pressed · squash 0.95",
                        ObjectState.Dragging to "Dragging · 1.08 · −6° · shadow",
                        ObjectState.DropTarget to "Drop target · sun dashed ring",
                        ObjectState.Correct to "Correct · mint + tick",
                        ObjectState.Wrong to "Wrong · red outline + shake",
                        ObjectState.HintHighlight to "Hint highlight · soft sun pulse",
                    ).forEach { (state, label) ->
                        KitSpec(label, modifier = Modifier.width(Sizes.PuzzleObjectPreview + Spacing.S)) {
                            key(if (state == ObjectState.Wrong) replay else 0) {
                                PuzzleObject(doodle = Doodles.block(PuzzleColors.Sky), state = state, modifier = Modifier.width(Sizes.PuzzleObjectPreview))
                            }
                        }
                    }
                }
                TextLink(text = "Replay the shake", onClick = { replay++ })
            }
        }
        KitSection(title = "Text & number tokens", caption = "Tappable number · draggable word (dashed = can be picked up)") {
            KitBoard {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.L), verticalAlignment = Alignment.CenterVertically) {
                    NumberToken(text = "9")
                    WordToken(text = tr("number", "संख्या"))
                }
            }
        }
        KitSection(
            title = "Matchstick",
            caption = "Idle · lifted · empty slot (red dashes). Wood #E8B66A is the only non-token color: matchstick-only.",
        ) {
            KitBoard {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XXL)) {
                    Matchstick(state = MatchstickState.Idle)
                    Matchstick(state = MatchstickState.Lifted)
                    Matchstick(state = MatchstickState.Slot)
                }
            }
        }
        KitSection(title = "Odd-one-out tile", caption = "Normal · found (mint) · wrong pick (red outline + shake). Tap the red one to replay.") {
            KitBoard {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp)) {
                    OddOneTile(state = OddTileState.Normal, contentDescription = "Star")
                    OddOneTile(state = OddTileState.Found, contentDescription = "Star, found")
                    key(oddWrong) {
                        OddOneTile(state = OddTileState.Wrong, contentDescription = "Star, wrong pick", onClick = { oddWrong++ })
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Device, sensor & media (canvas G2)
// ---------------------------------------------------------------------------------------------

private val cueLabels = mapOf(
    "shake" to ("Shake" to "हिलाओ"),
    "flipUpsideDown" to ("Flip upside down" to "उल्टा करो"),
    "faceDown" to ("Face down" to "उल्टा रखो"),
    "tiltLeft" to ("Tilt left" to "बाएँ झुकाओ"),
    "tiltRight" to ("Tilt right" to "दाएँ झुकाओ"),
    "volumeUp" to ("Volume up" to "आवाज़ बढ़ाओ"),
    "volumeDown" to ("Volume down" to "आवाज़ घटाओ"),
    "chargerConnected" to ("Plug in charger" to "चार्जर लगाओ"),
)

@Composable
fun DevicePage(onBack: () -> Unit) {
    KitPage(title = "Device & sensor", kicker = "Doodle Kit · Game pieces", onBack = onBack) {
        CaptionText(
            "Device tricks are hidden on purpose. These cues only appear inside hints (hint 2 shows the cue, hint 3 " +
                "the full move). Every trick has a tap fallback, so no player is stuck on a phone without that sensor.",
        )
        KitSection(
            title = "DeviceCue · 8 MVP tricks",
            caption = "120 tiles · phone = sky off-register · motion marks in red (graphics only, ≥ 3:1). Used inside the hint sheet and the tutorial.",
        ) {
            KitFlow {
                Doodles.deviceCues.forEach { (id, doodle) ->
                    val (en, hi) = cueLabels.getValue(id)
                    DeviceCueTile(doodle = doodle, label = tr(en, hi))
                }
            }
        }
        KitSection(
            title = "SensorFallback · never stuck",
            caption = "Shown after hint 3, or at once if the phone lacks the sensor. Also used with accessibility settings on.",
        ) {
            SensorFallbackCard(
                title = tr("Can't shake this phone?", "फ़ोन हिलाना मुश्किल है?"),
                body = tr("Some phones don't report shakes. Tap below to do it for you.", "कुछ फ़ोन हिलना नहीं पकड़ते। नीचे टैप करें, हम कर देंगे।"),
                actionText = tr("Shake for me", "मेरे लिए हिलाओ"),
                onAction = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
        KitSection(title = "Sound cues", caption = "Every puzzle stays solvable with sound off (captions for any sound clue).") {
            KitFlow {
                InkChip(text = tr("Sound helps here", "यहाँ आवाज़ मदद करेगी"), leadingIcon = PuzzleIcons.SoundOn)
                InkChip(text = tr("Sound is off", "आवाज़ बंद है"), fill = PuzzleColors.Sun, leadingIcon = PuzzleIcons.SoundOff)
            }
            StatusBanner(icon = PuzzleIcons.SoundOff, text = tr("This puzzle uses sound.", "इस पहेली में आवाज़ है।"), action = {
                TextLink(text = tr("Turn on", "चालू करें"), onClick = {})
            })
        }
        KitSection(
            title = "PermissionPrimer · camera & mic",
            caption = "Rule: explain before the Android permission dialog, say what is NOT collected, always offer Skip. Needs owner approval + privacy policy update before use.",
        ) {
            InkChip(text = "Later · not in MVP", fill = PuzzleColors.Sky)
            PermissionPrimer(
                icon = PuzzleIcons.Mic,
                iconFill = PuzzleColors.Sky,
                title = "Blow out the candle?",
                body = "This puzzle listens for a puff of air. Nothing is recorded or sent anywhere.",
                allowText = "Allow mic",
                onAllow = {},
                skipText = "Skip puzzle",
                onSkip = {},
                modifier = Modifier.fillMaxWidth(),
            )
            PermissionPrimer(
                icon = PuzzleIcons.Camera,
                iconFill = PuzzleColors.Sun,
                title = "Cover the camera",
                body = "We only check if it's dark. No photos are taken or saved.",
                allowText = "Allow camera",
                onAllow = {},
                skipText = "Skip puzzle",
                onSkip = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Celebration & rewards (canvas G3)
// ---------------------------------------------------------------------------------------------

@Composable
fun CelebratePage(onBack: () -> Unit) {
    var replay by remember { mutableIntStateOf(0) }
    KitPage(title = "Celebration", kicker = "Doodle Kit · Game pieces", onBack = onBack) {
        KitSection(title = "SolvedBurst", caption = "Mint badge pops 1 → 1.15 → 1, confetti doodles fly out 600 ms, success haptic.") {
            key(replay) { SolvedBurst(contentDescription = "Solved") }
            TextLink(text = "Replay", onClick = { replay++ })
        }
        KitSection(title = "Confetti doodles", caption = "6 shapes × 4 colors, ink outlines. 6–8 per burst, never more (calm, not casino).") {
            KitBoard { DoodleArt(Doodles.Confetti, Modifier.fillMaxWidth()) }
        }
        KitSection(title = "Result stats", caption = "Praise chips only state true facts. No fake ranks or invented percentages.") {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M + 2.dp)) {
                StatCard(label = tr("Time", "समय"), value = "0:42", modifier = Modifier.weight(1f))
                StatCard(label = tr("Hints", "संकेत"), value = tr("None", "कोई नहीं"), modifier = Modifier.weight(1f))
            }
            KitFlow {
                InkChip(text = tr("No hints!", "बिना संकेत!"), fill = PuzzleColors.Mint)
                InkChip(text = tr("Faster than yesterday", "कल से तेज़"), fill = PuzzleColors.Sun)
            }
        }
        KitSection(title = "Chapter complete") {
            ChapterCompleteCard(
                stampText = tr("DONE!", "हो गया!"),
                title = tr("Chapter 1", "अध्याय 1"),
                body = tr("40 puzzles. You're officially sneaky.", "40 पहेलियाँ। आप सच में चालाक हैं।"),
                contentDescription = "Chapter 1 complete",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        KitSection(title = "Streak milestone") {
            StreakCard(title = tr("7-day streak!", "7 दिन लगातार!"), body = tr("A full week of puzzles.", "पूरा हफ़्ता पहेलियाँ।"), modifier = Modifier.fillMaxWidth())
        }
        KitSection(title = "Sticker reward", caption = "Idea from direction E: earn a sticker per chapter. Not in MVP.") {
            InkChip(text = "Later", fill = PuzzleColors.Sky)
            InkCard(
                fill = PuzzleColors.Surface,
                offRegister = false,
                horizontalAlignment = Alignment.CenterHorizontally,
                radius = Radii.Card,
                modifier = Modifier.fillMaxWidth(),
            ) {
                StickerBadge(art = Doodles.FireSticker, contentDescription = "Fire sticker")
                Text(text = "Fire sticker", style = PuzzleTheme.type.headline)
            }
        }
    }
}
