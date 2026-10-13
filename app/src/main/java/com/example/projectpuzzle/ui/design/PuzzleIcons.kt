package com.example.projectpuzzle.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Stroke icons: 24 grid, 2.4 stroke, round caps/joins — design doc §8. Tint them with `Icon(tint = …)`. */
object PuzzleIcons {
    val Settings: ImageVector by lazy {
        strokeIcon(
            "settings",
            "M4 7h10", "M18 7h2", "M14 7a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
            "M4 17h2", "M10 17h10", "M6 17a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
        )
    }
    val Back: ImageVector by lazy { strokeIcon("back", "M15 5l-7 7 7 7") }
    val Hint: ImageVector by lazy {
        strokeIcon(
            "hint",
            "M9 18h6", "M10 22h4",
            "M12 2a7 7 0 0 0 -4 12.7c0.6 0.5 1 1.3 1 2.1V17h6v-0.2c0 -0.8 0.4 -1.6 1 -2.1A7 7 0 0 0 12 2z",
        )
    }
    val Restart: ImageVector by lazy { strokeIcon("restart", "M3 12a9 9 0 1 0 3 -6.7", "M3 4v5h5") }

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
