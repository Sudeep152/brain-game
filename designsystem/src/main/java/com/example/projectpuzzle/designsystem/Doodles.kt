package com.example.projectpuzzle.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser

/**
 * Doodle art = SVG-style paths copied 1:1 from the Doodle Kit canvas (G1, G2, C4), drawn with Compose.
 * One source of truth for objects, device cues and decorative art.
 *
 * Layer roles let states recolor art without new drawings:
 * [LayerRole.Body] main fill (Correct → mint) · [LayerRole.Outline] ink outline (Wrong → red) ·
 * [LayerRole.Detail] other fills · [LayerRole.Line] other strokes.
 */
enum class LayerRole { Body, Detail, Outline, Line }

@Immutable
data class DoodleLayer(
    val d: String,
    val role: LayerRole,
    val color: Color,
    val strokeWidth: Float = 3f,
    val rotation: Float = 0f,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f,
)

@Immutable
data class Doodle(val width: Float, val height: Float, val layers: List<DoodleLayer>)

/** Draws a [Doodle], scaled to the composable's width (height follows the aspect ratio). */
@Composable
fun DoodleArt(
    doodle: Doodle,
    modifier: Modifier = Modifier,
    bodyOverride: Color? = null,
    outlineOverride: Color? = null,
    outlineWidthOverride: Float? = null,
) {
    val paths = remember(doodle) { doodle.layers.map { PathParser().parsePathString(it.d).toPath() } }
    Canvas(modifier.aspectRatio(doodle.width / doodle.height)) {
        val factor = size.width / doodle.width
        scale(scale = factor, pivot = Offset.Zero) {
            doodle.layers.forEachIndexed { index, layer ->
                rotate(degrees = layer.rotation, pivot = Offset(layer.pivotX, layer.pivotY)) {
                    val stroke = Stroke(width = layer.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    when (layer.role) {
                        LayerRole.Body -> drawPath(paths[index], bodyOverride ?: layer.color)
                        LayerRole.Detail -> drawPath(paths[index], layer.color)
                        LayerRole.Outline -> drawPath(
                            paths[index],
                            outlineOverride ?: layer.color,
                            style = if (outlineWidthOverride != null) stroke.copyWidth(outlineWidthOverride) else stroke,
                        )
                        LayerRole.Line -> drawPath(paths[index], layer.color, style = stroke)
                    }
                }
            }
        }
    }
}

/** Path helpers so shapes read like the SVG source. */
internal fun circlePath(cx: Float, cy: Float, r: Float): String =
    "M${cx - r} $cy a$r $r 0 1 0 ${2 * r} 0 a$r $r 0 1 0 ${-2 * r} 0z"

internal fun roundRectPath(x: Float, y: Float, w: Float, h: Float, r: Float): String =
    "M${x + r} $y h${w - 2 * r} a$r $r 0 0 1 $r $r v${h - 2 * r} a$r $r 0 0 1 ${-r} $r " +
        "h${-(w - 2 * r)} a$r $r 0 0 1 ${-r} ${-r} v${-(h - 2 * r)} a$r $r 0 0 1 $r ${-r}z"

private fun body(d: String, color: Color) = DoodleLayer(d, LayerRole.Body, color)
private fun detail(d: String, color: Color) = DoodleLayer(d, LayerRole.Detail, color)
private fun outline(d: String, width: Float = 3f) = DoodleLayer(d, LayerRole.Outline, PuzzleColors.Ink, width)
private fun line(d: String, color: Color = PuzzleColors.Ink, width: Float = 3f) = DoodleLayer(d, LayerRole.Line, color, width)

object Doodles {
    // ---- Shape primitives (G1), viewBox 100 ----
    fun circle(fill: Color) = Doodle(100f, 100f, listOf(body(circlePath(54f, 54f, 36f), fill), outline(circlePath(48f, 48f, 36f))))
    fun roundRect(fill: Color) = Doodle(100f, 100f, listOf(body(roundRectPath(18f, 18f, 72f, 72f, 16f), fill), outline(roundRectPath(12f, 12f, 72f, 72f, 16f))))
    fun triangle(fill: Color) = Doodle(100f, 100f, listOf(body("M56 16 L94 86 L18 86 Z", fill), outline("M50 10 L88 80 L12 80 Z")))
    fun star(fill: Color) = Doodle(
        100f, 100f,
        listOf(
            body("M56 12l11 23 25 3-19 17 5 25-22-13-22 13 5-25-19-17 25-3z", fill),
            outline("M50 6l11 23 25 3-19 17 5 25-22-13-22 13 5-25-19-17 25-3z"),
        ),
    )
    fun blob(fill: Color) = Doodle(
        100f, 100f,
        listOf(
            body("M30 22c16-14 46-8 56 10s4 44-18 52-50-2-54-24 0-26 16-38z", fill),
            outline("M24 16c16-14 46-8 56 10s4 44-18 52-50-2-54-24 0-26 16-38z"),
        ),
    )

    // ---- Object library (G1) ----
    val Fire = Doodle(
        110f, 116f,
        listOf(
            body("M60 14 C 74 36, 96 46, 96 72 C 96 94, 80 106, 60 106 C 40 106, 24 94, 24 72 C 24 54, 38 46, 42 30 C 50 42, 54 48, 60 48 C 60 36, 58 26, 60 14 Z", PuzzleColors.Red),
            outline("M54 8 C 68 30, 90 40, 90 66 C 90 88, 74 100, 54 100 C 34 100, 18 88, 18 66 C 18 48, 32 40, 36 24 C 44 36, 48 42, 54 42 C 54 30, 52 20, 54 8 Z"),
            detail("M60 62 C 66 72, 74 76, 74 86 C 74 96, 68 100, 60 100 C 52 100, 46 96, 46 86 C 46 78, 54 72, 60 62 Z", PuzzleColors.Sun),
        ),
    )
    val Ice = Doodle(
        100f, 100f,
        listOf(
            body(roundRectPath(18f, 18f, 74f, 74f, 14f), PuzzleColors.Sky),
            outline(roundRectPath(12f, 12f, 74f, 74f, 14f)),
            line("M24 28h22M24 38h8"),
        ),
    )
    val Water = Doodle(
        100f, 100f,
        listOf(
            body("M56 16 C 66 36, 82 50, 82 66 a26 26 0 0 1 -52 0 C 30 50, 46 36, 56 16 Z", PuzzleColors.Sky),
            outline("M50 10 C 60 30, 76 44, 76 60 a26 26 0 0 1 -52 0 C 24 44, 40 30, 50 10 Z"),
        ),
    )
    val Leaf = Doodle(
        92f, 92f,
        listOf(
            body("M20 78 C 16 40, 40 18, 76 20 C 78 56, 58 80, 20 78 Z", PuzzleColors.Mint),
            outline("M14 72 C 10 34, 34 12, 70 14 C 72 50, 52 74, 14 72 Z"),
            line("M18 68 L 54 30"),
        ),
    )
    val Key = Doodle(
        100f, 100f,
        listOf(
            body(circlePath(36f, 40f, 18f), PuzzleColors.Sun),
            line("M50 46h36v12M74 46v10", PuzzleColors.Sun, 10f),
            outline(circlePath(30f, 34f, 18f)),
            line("M48 34h34M70 34v14M80 34v10"),
        ),
    )
    val Cloud = Doodle(
        100f, 100f,
        listOf(
            body("M26 74h52a18 18 0 0 0 0-36 24 24 0 0 0-46-4 20 20 0 0 0-6 40z", PuzzleColors.Surface),
            outline("M20 68h52a18 18 0 0 0 0-36 24 24 0 0 0-46-4 20 20 0 0 0-6 40z"),
        ),
    )
    val Sun = Doodle(
        100f, 100f,
        listOf(
            body(circlePath(56f, 56f, 22f), PuzzleColors.Sun),
            outline(circlePath(50f, 50f, 22f)),
            line("M50 10v10M50 80v10M10 50h10M80 50h10M22 22l7 7M71 71l7 7M78 22l-7 7M29 71l-7 7"),
        ),
    )
    val Lock = Doodle(
        100f, 100f,
        listOf(
            body(roundRectPath(26f, 46f, 56f, 44f, 8f), PuzzleColors.Red),
            outline(roundRectPath(20f, 40f, 56f, 44f, 8f)),
            line("M32 40V30a16 16 0 0 1 32 0v10"),
            detail(circlePath(48f, 60f, 4f), PuzzleColors.Ink),
        ),
    )
    val objects: List<Pair<String, Doodle>> = listOf(
        "fire" to Fire, "ice" to Ice, "water" to Water, "leaf" to Leaf,
        "key" to Key, "cloud" to Cloud, "sun" to Sun, "lock" to Lock,
    )

    // ---- Device cues (G2), viewBox 90 ----
    private val phoneFill = roundRectPath(34f, 20f, 30f, 54f, 7f)
    private val phoneOutline = roundRectPath(30f, 16f, 30f, 54f, 7f)
    private fun phone(rotation: Float = 0f) = listOf(
        DoodleLayer(phoneFill, LayerRole.Body, PuzzleColors.Sky, rotation = rotation, pivotX = 45f, pivotY = 45f),
        DoodleLayer(phoneOutline, LayerRole.Outline, PuzzleColors.Ink, rotation = rotation, pivotX = 45f, pivotY = 45f),
    )
    private fun red(d: String) = line(d, PuzzleColors.Red)

    val CueShake = Doodle(90f, 90f, phone() + line("M18 28c-4 6-4 14 0 20M12 22c-7 10-7 22 0 32M72 28c4 6 4 14 0 20M78 22c7 10 7 22 0 32"))
    val CueFlip = Doodle(
        90f, 90f,
        phone(180f) + DoodleLayer("M40 22h10", LayerRole.Line, PuzzleColors.Ink, rotation = 180f, pivotX = 45f, pivotY = 45f) +
            red("M16 30a30 30 0 0 1 22-18") + red("M32 8l8 4-5 7"),
    )
    val CueFaceDown = Doodle(
        90f, 90f,
        listOf(
            line("M14 66h66"),
            body(roundRectPath(22f, 52f, 50f, 12f, 5f), PuzzleColors.Sky),
            outline(roundRectPath(18f, 48f, 50f, 12f, 5f)),
            red("M43 18v20M35 30l8 8 8-8"),
        ),
    )
    val CueTiltLeft = Doodle(90f, 90f, phone(-20f) + red("M22 74c-6-6-8-14-6-22M14 58l2-7 7 2"))
    val CueTiltRight = Doodle(90f, 90f, phone(20f) + red("M68 74c6-6 8-14 6-22M76 58l-2-7-7 2"))
    val CueVolumeUp = Doodle(
        90f, 90f,
        phone() + detail(roundRectPath(58f, 26f, 6f, 12f, 2f), PuzzleColors.Red) +
            line(roundRectPath(58f, 26f, 6f, 12f, 2f), width = 2f) + red("M76 40V22M70 28l6-6 6 6"),
    )
    val CueVolumeDown = Doodle(
        90f, 90f,
        phone() + detail(roundRectPath(58f, 42f, 6f, 12f, 2f), PuzzleColors.Red) +
            line(roundRectPath(58f, 42f, 6f, 12f, 2f), width = 2f) + red("M76 40v18M70 52l6 6 6-6"),
    )
    val CueCharger = Doodle(
        90f, 90f,
        listOf(
            body(roundRectPath(34f, 12f, 30f, 54f, 7f), PuzzleColors.Sky),
            outline(roundRectPath(30f, 8f, 30f, 54f, 7f)),
            line("M45 62v8M45 70c0 10 10 8 14 14"),
            red("M42 30l-4 10h8l-4 10"),
        ),
    )
    val deviceCues: List<Pair<String, Doodle>> = listOf(
        "shake" to CueShake, "flipUpsideDown" to CueFlip, "faceDown" to CueFaceDown, "tiltLeft" to CueTiltLeft,
        "tiltRight" to CueTiltRight, "volumeUp" to CueVolumeUp, "volumeDown" to CueVolumeDown, "chargerConnected" to CueCharger,
    )

    // ---- Decorative art (C4, S1, Home) ----
    // Loader pieces = the S7 "waiting" art (32 units each, bottom-aligned, 14 apart).
    val LoaderCircle = Doodle(32f, 32f, listOf(detail(circlePath(16f, 16f, 16f), PuzzleColors.Red), outline(circlePath(16f, 16f, 16f), 2.5f)))
    val LoaderSquare = Doodle(32f, 32f, listOf(detail(roundRectPath(0f, 0f, 32f, 32f, 8f), PuzzleColors.Sun), outline(roundRectPath(0f, 0f, 32f, 32f, 8f), 2.5f)))
    val LoaderTriangle = Doodle(32f, 32f, listOf(detail("M0 32 L16 2 L32 32 Z", PuzzleColors.Mint), outline("M0 32 L16 2 L32 32 Z", 2.5f)))
    val loaderPieces: List<Doodle> = listOf(LoaderCircle, LoaderSquare, LoaderTriangle)

    val EmptyCalendar = Doodle(
        80f, 70f,
        listOf(
            body(roundRectPath(16f, 14f, 52f, 46f, 10f), PuzzleColors.Sky),
            outline(roundRectPath(10f, 8f, 52f, 46f, 10f), 2.5f),
            line("M10 20h52", width = 2.5f),
            line("M22 2v10M50 2v10", width = 2.5f),
        ),
    )
    val ErrorMark = Doodle(
        80f, 70f,
        listOf(
            body(circlePath(44f, 40f, 26f), PuzzleColors.Red),
            outline(circlePath(38f, 34f, 26f), 2.5f),
            line("M30 26l16 16M46 26L30 42", width = 2.6f),
        ),
    )
    val Confetti = Doodle(
        300f, 200f,
        listOf(
            detail(circlePath(40f, 50f, 14f), PuzzleColors.Red), line(circlePath(40f, 50f, 14f), width = 2.5f),
            detail("M110 30l18 30h-36z", PuzzleColors.Mint), line("M110 30l18 30h-36z", width = 2.5f),
            DoodleLayer(roundRectPath(172f, 34f, 28f, 28f, 6f), LayerRole.Detail, PuzzleColors.Sky, rotation = 14f, pivotX = 186f, pivotY = 48f),
            DoodleLayer(roundRectPath(172f, 34f, 28f, 28f, 6f), LayerRole.Line, PuzzleColors.Ink, 2.5f, rotation = 14f, pivotX = 186f, pivotY = 48f),
            detail("M250 30l6 13 14 1-10 9 3 14-13-8-13 8 3-14-10-9 14-1z", PuzzleColors.Sun), line("M250 30l6 13 14 1-10 9 3 14-13-8-13 8 3-14-10-9 14-1z", width = 2f),
            line("M30 140c10-14 20 14 30 0s20 14 30 0"),
            detail(circlePath(150f, 140f, 9f), PuzzleColors.Sun), line(circlePath(150f, 140f, 9f), width = 2.5f),
            detail("M210 120l14 24h-28z", PuzzleColors.Red), line("M210 120l14 24h-28z", width = 2.5f),
            line("M250 150c8-8 16 8 24 0"),
        ),
    )
    /** Confetti around the solved badge (S2 / G3 SolvedBurst), viewBox 346 × 240, badge sits in the middle. */
    val BurstConfetti = Doodle(
        346f, 240f,
        listOf(
            detail(circlePath(46f, 56f, 11f), PuzzleColors.Red), line(circlePath(46f, 56f, 11f), width = 2.5f),
            detail("M282 34l14 24h-28z", PuzzleColors.Mint), line("M282 34l14 24h-28z", width = 2.5f),
            DoodleLayer(roundRectPath(286f, 170f, 20f, 20f, 4f), LayerRole.Detail, PuzzleColors.Sky, rotation = 18f, pivotX = 296f, pivotY = 180f),
            DoodleLayer(roundRectPath(286f, 170f, 20f, 20f, 4f), LayerRole.Line, PuzzleColors.Ink, 2.5f, rotation = 18f, pivotX = 296f, pivotY = 180f),
            line("M30 186c8-10 16 10 24 0s16 10 24 0"),
            detail(circlePath(173f, 12f, 8f), PuzzleColors.Sun), line(circlePath(173f, 12f, 8f), width = 2.5f),
            detail("M318 110l4 9 9 1-7 6 2 9-8-5-8 5 2-9-7-6 9-1z", PuzzleColors.Sun), line("M318 110l4 9 9 1-7 6 2 9-8-5-8 5 2-9-7-6 9-1z", width = 2f),
        ),
    )

    /** Plain 74-unit block used to show object states (G1), viewBox 100. */
    fun block(fill: Color) = Doodle(100f, 100f, listOf(body(roundRectPath(18f, 18f, 74f, 74f, 14f), fill), outline(roundRectPath(12f, 12f, 74f, 74f, 14f))))

    /** "line / path" primitive (G1), viewBox 100. */
    val Squiggle = Doodle(100f, 100f, listOf(line("M14 84 C 34 40, 60 70, 86 16")))

    /** Small star used in odd-one-out tiles (G1), viewBox 24. */
    val TileStar = Doodle(
        24f, 24f,
        listOf(
            body("M12 2.5l2.9 6.4 7 .6-5.3 4.7 1.6 6.9L12 17.4l-6.2 3.7 1.6-6.9L2.1 9.5l7-.6z", PuzzleColors.Sun),
            outline("M12 2.5l2.9 6.4 7 .6-5.3 4.7 1.6 6.9L12 17.4l-6.2 3.7 1.6-6.9L2.1 9.5l7-.6z", 1.4f),
        ),
    )

    /** Off-register star on the streak milestone card (G3), viewBox 24. */
    val StreakStar = Doodle(
        24f, 24f,
        listOf(
            body("M12.8 2.8l3 6.6 7.2.7-5.4 4.8 1.6 7.1-6.4-3.8-6.4 3.8 1.6-7.1L2.6 10.1l7.2-.7z", PuzzleColors.Sun),
            outline("M12 2l3 6.6 7.2.7-5.4 4.8 1.6 7.1L12 17.4l-6.4 3.8 1.6-7.1L1.8 9.3 9 8.6z", 1.2f),
        ),
    )

    /** Fire drawn as one sticker shape (G3 sticker reward, later), viewBox 110 × 116. */
    val FireSticker = Doodle(
        110f, 116f,
        listOf(
            body("M54 8 C 68 30, 90 40, 90 66 C 90 88, 74 100, 54 100 C 34 100, 18 88, 18 66 C 18 48, 32 40, 36 24 C 44 36, 48 42, 54 42 C 54 30, 52 20, 54 8 Z", PuzzleColors.Sun),
            outline("M54 8 C 68 30, 90 40, 90 66 C 90 88, 74 100, 54 100 C 34 100, 18 88, 18 66 C 18 48, 32 40, 36 24 C 44 36, 48 42, 54 42 C 54 30, 52 20, 54 8 Z", 5f),
        ),
    )

    // ---- Feedback bubble glyphs (C4), viewBox 24 ----
    val FeedbackNope = Doodle(24f, 24f, listOf(detail(circlePath(12f, 12f, 10f), PuzzleColors.Red), line("M8 8l8 8M16 8l-8 8", PuzzleColors.Surface, 2.6f)))
    val FeedbackClose = Doodle(24f, 24f, listOf(line("M4 12c2-3 4-3 6 0s4 3 6 0 4-3 4-3", width = 2.6f)))
    val FeedbackYes = Doodle(24f, 24f, listOf(line("M5 12.5l4.5 4.5L19 7.5", width = 3f)))

    /** First-launch hero (S6): smiling circle, tilted square, triangle, squiggle. viewBox 346 × 250. */
    val WelcomeHero = Doodle(
        346f, 250f,
        listOf(
            detail(circlePath(112f, 104f, 62f), PuzzleColors.Red),
            line(circlePath(106f, 98f, 62f)),
            detail("M218 196 L262 116 L306 196 Z", PuzzleColors.Mint),
            line("M212 190 L256 110 L300 190 Z"),
            DoodleLayer(roundRectPath(196f, 36f, 70f, 70f, 14f), LayerRole.Detail, PuzzleColors.Sun, rotation = 12f, pivotX = 231f, pivotY = 71f),
            DoodleLayer(roundRectPath(190f, 30f, 70f, 70f, 14f), LayerRole.Line, PuzzleColors.Ink, rotation = 12f, pivotX = 225f, pivotY = 65f),
            line("M40 222c12-14 24 14 36 0s24 14 36 0"),
            detail(circlePath(86f, 88f, 6f), PuzzleColors.Ink),
            detail(circlePath(122f, 88f, 6f), PuzzleColors.Ink),
            line("M88 118c10 10 26 10 36 0"),
        ),
    )

    /** Red circle + mint triangle used on the Home hero card (mockup D-Home). */
    val HeroShapes = Doodle(
        96f, 80f,
        listOf(
            detail(circlePath(66f, 28f, 18f), PuzzleColors.Red), line(circlePath(60f, 22f, 18f), width = 2.5f),
            detail("M18 70 L36 40 L54 70 Z", PuzzleColors.Mint), line("M12 64 L30 34 L48 64 Z", width = 2.5f),
        ),
    )
}

private fun Stroke.copyWidth(width: Float) = Stroke(width = width, cap = cap, join = join)
