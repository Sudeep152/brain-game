package com.example.projectpuzzle.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class Level(
    val schemaVersion: Int,
    val id: String,
    val family: LevelFamily,
    val runtime: String,
    val minAppVersion: Int,
    val difficulty: Int,
    val languageDependent: Boolean,
    val locales: List<String>,
    val text: LevelText,
    val hints: List<Hint>,
    val state: Map<String, JsonPrimitive> = emptyMap(),
    val scene: Scene? = null,
    val rules: List<Rule>? = null,
    val win: Condition? = null,
    val params: JsonObject? = null,
    val solution: List<SolutionStep>? = null,
    val altSolutions: List<List<SolutionStep>> = emptyList(),
    val meta: LevelMeta? = null,
)

@Serializable
enum class LevelFamily {
    @SerialName("teaser") TEASER,
    @SerialName("combine") COMBINE,
    @SerialName("matchstick") MATCHSTICK,
    @SerialName("oddoneout") ODD_ONE_OUT,
}

@Serializable
data class LevelText(val prompt: String)

@Serializable
data class Hint(
    val textKey: String,
    val highlight: String? = null,
    val showSolution: Boolean = false,
)

@Serializable
data class LevelMeta(
    val author: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: String? = null,
)

@Serializable
data class Scene(
    val width: Int,
    val height: Int,
    val background: String,
    val objects: List<SceneObject>,
)

@Serializable
data class SceneObject(
    val id: String,
    val kind: ObjectKind,
    val textKey: String? = null,
    val text: String? = null,
    val asset: String? = null,
    val shape: ShapeKind? = null,
    val color: String? = null,
    val x: Double,
    val y: Double,
    val w: Double,
    val h: Double,
    val rotation: Double = 0.0,
    val z: Int = 0,
    val visible: Boolean = true,
    val tappable: Boolean = false,
    val draggable: Boolean = false,
    val snapBack: Boolean = false,
    val tags: List<String> = emptyList(),
)

@Serializable
enum class ObjectKind {
    @SerialName("sprite") SPRITE,
    @SerialName("text") TEXT,
    @SerialName("shape") SHAPE,
}

@Serializable
enum class ShapeKind {
    @SerialName("rect") RECT,
    @SerialName("circle") CIRCLE,
    @SerialName("line") LINE,
}

@Serializable
data class Rule(
    val id: String,
    val on: Trigger,
    @SerialName("if") val conditions: List<Condition> = emptyList(),
    @SerialName("do") val actions: List<Action>,
    val once: Boolean = false,
)

@Serializable sealed interface Trigger

@Serializable @SerialName("tap")
data class TapTrigger(val target: String) : Trigger

@Serializable @SerialName("longPress")
data class LongPressTrigger(val target: String) : Trigger

@Serializable @SerialName("dragEnd")
data class DragEndTrigger(val target: String, val dropOn: String? = null) : Trigger

@Serializable @SerialName("dragOut")
data class DragOutTrigger(val target: String, val edge: Edge) : Trigger

@Serializable @SerialName("multiTouch")
data class MultiTouchTrigger(val count: Int, val target: String? = null) : Trigger

@Serializable @SerialName("device")
data class DeviceTrigger(val event: DeviceEvent) : Trigger

@Serializable @SerialName("timer")
data class TimerTrigger(val id: String) : Trigger

@Serializable
enum class Edge {
    @SerialName("top") TOP,
    @SerialName("bottom") BOTTOM,
    @SerialName("left") LEFT,
    @SerialName("right") RIGHT,
    @SerialName("any") ANY,
}

@Serializable
enum class DeviceEvent {
    @SerialName("shake") SHAKE,
    @SerialName("flipUpsideDown") FLIP_UPSIDE_DOWN,
    @SerialName("faceDown") FACE_DOWN,
    @SerialName("tiltLeft") TILT_LEFT,
    @SerialName("tiltRight") TILT_RIGHT,
    @SerialName("volumeUp") VOLUME_UP,
    @SerialName("volumeDown") VOLUME_DOWN,
    @SerialName("chargerConnected") CHARGER_CONNECTED,
}

@Serializable sealed interface Condition

@Serializable @SerialName("stateEquals")
data class StateEqualsCondition(val key: String, val value: JsonPrimitive) : Condition

@Serializable @SerialName("stateAtLeast")
data class StateAtLeastCondition(val key: String, val value: Int) : Condition

@Serializable @SerialName("objectVisible")
data class ObjectVisibleCondition(val target: String) : Condition

@Serializable @SerialName("objectOverlaps")
data class ObjectOverlapsCondition(val a: String, val b: String) : Condition

@Serializable @SerialName("allOf")
data class AllOfCondition(val conditions: List<Condition>) : Condition

@Serializable @SerialName("anyOf")
data class AnyOfCondition(val conditions: List<Condition>) : Condition

@Serializable @SerialName("not")
data class NotCondition(val condition: Condition) : Condition

@Serializable sealed interface Action

@Serializable @SerialName("setState")
data class SetStateAction(val key: String, val value: JsonPrimitive) : Action

@Serializable @SerialName("incState")
data class IncStateAction(val key: String, val by: Int = 1) : Action

@Serializable @SerialName("show")
data class ShowAction(val target: String) : Action

@Serializable @SerialName("hide")
data class HideAction(val target: String) : Action

@Serializable @SerialName("moveTo")
data class MoveToAction(val target: String, val x: Double, val y: Double, val ms: Int = 300) : Action

@Serializable @SerialName("swapAsset")
data class SwapAssetAction(val target: String, val asset: String) : Action

@Serializable @SerialName("animate")
data class AnimateAction(val target: String, val anim: AnimationKind) : Action

@Serializable @SerialName("sound")
data class SoundAction(val id: String) : Action

@Serializable @SerialName("haptic")
data class HapticAction(val id: HapticKind) : Action

@Serializable @SerialName("feedback")
data class FeedbackAction(val kind: FeedbackKind) : Action

@Serializable @SerialName("startTimer")
data class StartTimerAction(val id: String, val ms: Long) : Action

@Serializable @SerialName("cancelTimer")
data class CancelTimerAction(val id: String) : Action

@Serializable @SerialName("win")
data object WinAction : Action

@Serializable
enum class AnimationKind {
    @SerialName("wiggle") WIGGLE,
    @SerialName("pop") POP,
    @SerialName("spin") SPIN,
    @SerialName("bounce") BOUNCE,
    @SerialName("fadeIn") FADE_IN,
    @SerialName("fadeOut") FADE_OUT,
}

@Serializable
enum class HapticKind {
    @SerialName("light") LIGHT,
    @SerialName("medium") MEDIUM,
    @SerialName("success") SUCCESS,
    @SerialName("error") ERROR,
}

@Serializable
enum class FeedbackKind {
    @SerialName("nope") NOPE,
    @SerialName("close") CLOSE,
    @SerialName("yes") YES,
}

@Serializable sealed interface SolutionStep

@Serializable @SerialName("tap")
data class TapStep(val target: String) : SolutionStep

@Serializable @SerialName("longPress")
data class LongPressStep(val target: String) : SolutionStep

@Serializable @SerialName("drag")
data class DragStep(
    val target: String,
    val toObject: String? = null,
    val x: Double? = null,
    val y: Double? = null,
) : SolutionStep

@Serializable @SerialName("multiTouch")
data class MultiTouchStep(val count: Int, val target: String? = null) : SolutionStep

@Serializable @SerialName("device")
data class DeviceStep(val event: DeviceEvent) : SolutionStep

@Serializable @SerialName("timer")
data class TimerStep(val id: String) : SolutionStep

@Serializable
data class MatchstickParams(val equation: String, val moves: Int, val allowedOps: List<String>)

@Serializable
data class OddOneOutParams(
    val seed: Long,
    val rows: Int,
    val cols: Int,
    val base: String,
    val variation: OddOneOutVariation,
    val timeLimitMs: Long? = null,
)

@Serializable
data class OddOneOutVariation(val type: VariationKind, val delta: Double)

@Serializable
enum class VariationKind {
    @SerialName("rotation") ROTATION,
    @SerialName("color") COLOR,
    @SerialName("size") SIZE,
    @SerialName("shape") SHAPE,
}

@Serializable
data class AssetCatalog(val schemaVersion: Int, val assets: Map<String, AssetEntry>)

@Serializable
data class AssetEntry(val kind: AssetKind, val file: String)

@Serializable
enum class AssetKind {
    @SerialName("background") BACKGROUND,
    @SerialName("sprite") SPRITE,
    @SerialName("sound") SOUND,
}
