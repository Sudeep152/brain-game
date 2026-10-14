package com.example.projectpuzzle.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Stroke icons: 24 grid, 2.4 stroke, round caps/joins — design doc §8, Doodle Kit F2.
 * Tint with `Icon(tint = …)`. Paths are copied 1:1 from the Doodle Kit canvas.
 */
object PuzzleIcons {
    val Settings: ImageVector by lazy {
        strokeIcon(
            "settings",
            "M4 7h10", "M18 7h2", "M14 7a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
            "M4 17h2", "M10 17h10", "M6 17a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
        )
    }
    val Back: ImageVector by lazy { strokeIcon("back", "M15 5l-7 7 7 7") }
    val Chevron: ImageVector by lazy { strokeIcon("chevron", "M9 5l7 7-7 7") }
    val Hint: ImageVector by lazy {
        strokeIcon(
            "hint",
            "M9 18h6", "M10 22h4",
            "M12 2a7 7 0 0 0 -4 12.7c0.6 0.5 1 1.3 1 2.1V17h6v-0.2c0 -0.8 0.4 -1.6 1 -2.1A7 7 0 0 0 12 2z",
        )
    }
    val Restart: ImageVector by lazy { strokeIcon("restart", "M3 12a9 9 0 1 0 3 -6.7", "M3 4v5h5") }
    val Home: ImageVector by lazy { strokeIcon("home", "M3 11l9 -7 9 7", "M5 10v10h14V10") }
    val Share: ImageVector by lazy {
        strokeIcon(
            "share",
            "M15 5a3 3 0 1 0 6 0a3 3 0 1 0 -6 0", "M3 12a3 3 0 1 0 6 0a3 3 0 1 0 -6 0",
            "M15 19a3 3 0 1 0 6 0a3 3 0 1 0 -6 0", "M8.6 13.5l6.8 4", "M15.4 6.5l-6.8 4",
        )
    }
    val SoundOn: ImageVector by lazy { strokeIcon("soundOn", "M4 9v6h4l5 4V5L8 9H4z", "M16 8.5a5 5 0 0 1 0 7", "M19 6a8.5 8.5 0 0 1 0 12") }
    val SoundOff: ImageVector by lazy { strokeIcon("soundOff", "M4 9v6h4l5 4V5L8 9H4z", "M17 9l5 6", "M22 9l-5 6") }
    val Haptics: ImageVector by lazy {
        strokeIcon("haptics", "M9 3h6a1 1 0 0 1 1 1v16a1 1 0 0 1 -1 1H9a1 1 0 0 1 -1 -1V4a1 1 0 0 1 1 -1z", "M4 8v8", "M20 8v8")
    }
    val Language: ImageVector by lazy {
        strokeIcon("language", "M4 5h9", "M8.5 3v2", "M6 5c0 4 3 7 6 8", "M11 5c-1 4 -4 7 -7 8", "M13 21l4 -9 4 9", "M14.5 18h5")
    }
    val Lock: ImageVector by lazy { strokeIcon("lock", "M6 11h12v10H6z", "M8 11V8a4 4 0 0 1 8 0v3") }
    val Check: ImageVector by lazy { strokeIcon("check", "M5 12.5l4.5 4.5L19 7.5") }
    val Close: ImageVector by lazy { strokeIcon("close", "M6 6l12 12", "M18 6L6 18") }
    val Play: ImageVector by lazy { strokeIcon("play", "M8 5l11 7 -11 7z") }
    val Star: ImageVector by lazy { strokeIcon("star", "M12 3l2.6 5.6 6.1 0.6 -4.6 4.1 1.3 6 -5.4 -3.1 -5.4 3.1 1.3 -6 -4.6 -4.1 6.1 -0.6z") }
    val Timer: ImageVector by lazy { strokeIcon("timer", "M4 13a8 8 0 1 0 16 0a8 8 0 1 0 -16 0", "M12 9v4l3 2", "M10 2h4") }
    val Camera: ImageVector by lazy { strokeIcon("camera", "M4 8h3l2 -3h6l2 3h3v11H4z", "M8.5 13a3.5 3.5 0 1 0 7 0a3.5 3.5 0 1 0 -7 0") }
    val Mic: ImageVector by lazy { strokeIcon("mic", "M9 5a3 3 0 0 1 6 0v6a3 3 0 0 1 -6 0z", "M5 11a7 7 0 0 0 14 0", "M12 18v3") }
    val Info: ImageVector by lazy { strokeIcon("info", "M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0", "M12 11v5", "M12 8h0.01") }
    val Calendar: ImageVector by lazy { strokeIcon("calendar", "M4 6h16v14H4z", "M4 10h16", "M8 3v4", "M16 3v4") }
    val WifiOff: ImageVector by lazy {
        strokeIcon("wifiOff", "M2 8.5a15 15 0 0 1 20 0", "M5.5 12a10 10 0 0 1 13 0", "M9 15.5a5 5 0 0 1 6 0", "M3 3l18 18")
    }
    val Report: ImageVector by lazy { strokeIcon("report", "M4 5h16v11H9l-5 4z") }
    val Privacy: ImageVector by lazy { strokeIcon("privacy", "M12 3l8 3v6c0 5 -3.5 8 -8 9c-4.5 -1 -8 -4 -8 -9V6z") }

    /** Every icon with its name — used by the Doodle Kit icon page. */
    val all: List<Pair<String, ImageVector>> by lazy {
        listOf(
            "settings" to Settings, "back" to Back, "chevron" to Chevron, "hint" to Hint, "restart" to Restart,
            "home" to Home, "share" to Share, "soundOn" to SoundOn, "soundOff" to SoundOff, "haptics" to Haptics,
            "language" to Language, "lock" to Lock, "check" to Check, "close" to Close, "play" to Play,
            "star" to Star, "timer" to Timer, "camera" to Camera, "mic" to Mic, "info" to Info,
            "calendar" to Calendar, "wifiOff" to WifiOff, "report" to Report, "privacy" to Privacy, "playFilled" to PlayFilled,
        )
    }

    /** Filled play triangle for "watch video" actions (Doodle Kit C4 / S3). */
    val PlayFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "playFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(pathData = addPathNodes("M7 4l13 8-13 8z"), fill = SolidColor(Color.Black))
        }.build()
    }

    private fun strokeIcon(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            paths.forEach { d ->
                addPath(
                    pathData = addPathNodes(d),
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2.4f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
}
