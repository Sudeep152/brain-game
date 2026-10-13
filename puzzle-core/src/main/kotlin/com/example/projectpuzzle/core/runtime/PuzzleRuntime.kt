package com.example.projectpuzzle.core.runtime

import com.example.projectpuzzle.core.model.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

enum class SessionStatus { NOT_LOADED, LOADING, READY, PLAYING, PAUSED, COMPLETED, FAILED, ERROR }

sealed interface RuntimeInput {
    data object Start : RuntimeInput
    data object Pause : RuntimeInput
    data object Resume : RuntimeInput
    data class Tap(val id: String) : RuntimeInput
    data class LongPress(val id: String) : RuntimeInput
    data class DragStart(val id: String) : RuntimeInput
    data class DragMove(val id: String, val x: Double, val y: Double) : RuntimeInput
    data class DragEnd(val id: String, val x: Double, val y: Double) : RuntimeInput
    data class MultiTouch(val count: Int, val id: String? = null) : RuntimeInput
    data class Device(val event: DeviceEvent) : RuntimeInput
    data class TimerFired(val id: String) : RuntimeInput
    data object HintRequested : RuntimeInput
}

sealed interface PuzzleEffect {
    data class PlaySound(val id: String) : PuzzleEffect
    data class Haptic(val kind: HapticKind) : PuzzleEffect
    data class Animate(val target: String, val animation: String, val durationMs: Int? = null) : PuzzleEffect
    data class Feedback(val kind: FeedbackKind) : PuzzleEffect
    data class ScheduleTimer(val id: String, val ms: Long) : PuzzleEffect
    data class CancelTimer(val id: String) : PuzzleEffect
    data class ShowHint(val index: Int) : PuzzleEffect
    data object ShowSolution : PuzzleEffect
    data object LevelCompleted : PuzzleEffect
}

data class RuntimeLog(val input: RuntimeInput, val reason: String)

data class ObjectState(
    val definition: SceneObject,
    val x: Double = definition.x,
    val y: Double = definition.y,
    val visible: Boolean = definition.visible,
    val asset: String? = definition.asset,
) {
    fun contains(px: Double, py: Double): Boolean = visible &&
        px >= x - definition.w / 2 && px <= x + definition.w / 2 &&
        py >= y - definition.h / 2 && py <= y + definition.h / 2

    fun overlaps(other: ObjectState): Boolean = visible && other.visible &&
        x - definition.w / 2 <= other.x + other.definition.w / 2 &&
        x + definition.w / 2 >= other.x - other.definition.w / 2 &&
        y - definition.h / 2 <= other.y + other.definition.h / 2 &&
        y + definition.h / 2 >= other.y - other.definition.h / 2
}

data class SceneState(val width: Int, val height: Int, val objects: List<ObjectState>) {
    fun objectById(id: String): ObjectState? = objects.firstOrNull { it.definition.id == id }

    fun hitTest(x: Double, y: Double, excluding: String? = null): ObjectState? = objects
        .withIndex()
        .filter { it.value.definition.id != excluding && it.value.contains(x, y) }
        .maxWithOrNull(compareBy<IndexedValue<ObjectState>> { it.value.definition.z }.thenBy { it.index })
        ?.value

    fun update(id: String, transform: (ObjectState) -> ObjectState): SceneState =
        copy(objects = objects.map { if (it.definition.id == id) transform(it) else it })
}

data class PuzzleState(
    val status: SessionStatus,
    val level: Level? = null,
    val variables: Map<String, JsonPrimitive> = emptyMap(),
    val scene: SceneState? = null,
    val firedOnceRules: Set<String> = emptySet(),
    val activeTimers: Set<String> = emptySet(),
    val dragOrigins: Map<String, Pair<Double, Double>> = emptyMap(),
    val hintIndex: Int = 0,
    val error: String? = null,
    val logs: List<RuntimeLog> = emptyList(),
)

data class SessionResult(val session: PuzzleSession, val effects: List<PuzzleEffect> = emptyList())

data class PuzzleSession(val state: PuzzleState = PuzzleState(SessionStatus.NOT_LOADED)) {
    companion object {
        fun ready(level: Level): PuzzleSession = PuzzleSession(level.toReadyState())
    }

    fun beginLoading(): PuzzleSession = if (state.status == SessionStatus.NOT_LOADED) {
        copy(state = state.copy(status = SessionStatus.LOADING))
    } else this

    fun finishLoading(level: Level): PuzzleSession = if (state.status == SessionStatus.LOADING) {
        PuzzleSession(level.toReadyState())
    } else this

    fun failLoading(message: String): PuzzleSession = if (state.status == SessionStatus.LOADING) {
        copy(state = state.copy(status = SessionStatus.ERROR, error = message))
    } else this

    fun handle(input: RuntimeInput): SessionResult {
        if (state.status == SessionStatus.COMPLETED) return ignored(input, "level already completed")
        return when (input) {
            RuntimeInput.Start -> if (state.status == SessionStatus.READY) {
                SessionResult(copy(state = state.copy(status = SessionStatus.PLAYING)))
            } else ignored(input, "Start requires READY")
            RuntimeInput.Pause -> if (state.status == SessionStatus.PLAYING) {
                SessionResult(copy(state = state.copy(status = SessionStatus.PAUSED)))
            } else ignored(input, "Pause requires PLAYING")
            RuntimeInput.Resume -> if (state.status == SessionStatus.PAUSED) {
                SessionResult(copy(state = state.copy(status = SessionStatus.PLAYING)))
            } else ignored(input, "Resume requires PAUSED")
            else -> if (state.status == SessionStatus.PLAYING) handleGameplay(input)
            else ignored(input, "gameplay input requires PLAYING")
        }
    }

    private fun handleGameplay(input: RuntimeInput): SessionResult = when (input) {
        is RuntimeInput.Tap -> {
            val target = state.scene?.objectById(input.id)
            if (target?.visible == true && target.definition.tappable) processOccurrence(Occurrence.Tap(input.id), input)
            else ignored(input, "tap target is not visible and tappable")
        }
        is RuntimeInput.LongPress -> {
            val target = state.scene?.objectById(input.id)
            if (target?.visible == true) processOccurrence(Occurrence.LongPress(input.id), input)
            else ignored(input, "long-press target is not visible")
        }
        is RuntimeInput.DragStart -> {
            val target = state.scene?.objectById(input.id)
            if (target?.visible == true && target.definition.draggable) {
                val next = state.copy(
                    dragOrigins = state.dragOrigins + (input.id to (target.x to target.y)),
                )
                finishInput(next, emptyList())
            } else ignored(input, "drag target is not visible and draggable")
        }
        is RuntimeInput.DragMove -> moveDraggedObject(input.id, input.x, input.y, input)
        is RuntimeInput.DragEnd -> endDrag(input)
        is RuntimeInput.MultiTouch -> {
            val targetVisible = input.id == null || state.scene?.objectById(input.id)?.visible == true
            if (input.count in 2..5 && targetVisible) processOccurrence(Occurrence.MultiTouch(input.count, input.id), input)
            else ignored(input, "invalid multi-touch input")
        }
        is RuntimeInput.Device -> processOccurrence(Occurrence.Device(input.event), input)
        is RuntimeInput.TimerFired -> if (input.id in state.activeTimers) {
            copy(state = state.copy(activeTimers = state.activeTimers - input.id))
                .processOccurrence(Occurrence.Timer(input.id), input)
        } else ignored(input, "unknown or cancelled timer")
        RuntimeInput.HintRequested -> showHint()
        RuntimeInput.Start, RuntimeInput.Pause, RuntimeInput.Resume -> ignored(input, "invalid gameplay input")
    }

    private fun moveDraggedObject(id: String, x: Double, y: Double, input: RuntimeInput): SessionResult {
        if (id !in state.dragOrigins) return ignored(input, "drag was not started")
        val scene = state.scene ?: return ignored(input, "scene is unavailable")
        val next = state.copy(scene = scene.update(id) { it.copy(x = x, y = y) })
        return finishInput(next, emptyList())
    }

    private fun endDrag(input: RuntimeInput.DragEnd): SessionResult {
        val origin = state.dragOrigins[input.id] ?: return ignored(input, "drag was not started")
        val scene = state.scene ?: return ignored(input, "scene is unavailable")
        val movedScene = scene.update(input.id) { it.copy(x = input.x, y = input.y) }
        val moved = movedScene.objectById(input.id) ?: return ignored(input, "drag target is unknown")
        val base = copy(state = state.copy(scene = movedScene, dragOrigins = state.dragOrigins - input.id))
        val edge = crossedEdge(movedScene, input.x, input.y)
        val occurrence = if (edge != null) Occurrence.DragOut(input.id, edge) else Occurrence.DragEnd(
            input.id,
            movedScene.hitTest(input.x, input.y, excluding = input.id)?.definition?.id,
        )
        val processed = base.applyRules(occurrence)
        var nextState = processed.state
        if (!processed.matched && moved.definition.snapBack) {
            nextState = nextState.copy(scene = nextState.scene?.update(input.id) {
                it.copy(x = origin.first, y = origin.second)
            })
        }
        return base.finishInput(nextState, processed.effects)
    }

    private fun processOccurrence(occurrence: Occurrence, input: RuntimeInput): SessionResult {
        val processed = applyRules(occurrence)
        return finishInput(processed.state, processed.effects)
    }

    private fun finishInput(candidate: PuzzleState, effects: List<PuzzleEffect>): SessionResult {
        if (candidate.status == SessionStatus.COMPLETED) return SessionResult(copy(state = candidate), effects)
        val win = candidate.level?.win
        return if (win != null && evaluate(win, candidate)) {
            SessionResult(copy(state = candidate.copy(status = SessionStatus.COMPLETED)), effects + PuzzleEffect.LevelCompleted)
        } else SessionResult(copy(state = candidate), effects)
    }

    private fun applyRules(occurrence: Occurrence): RuleResult {
        var current = state
        val effects = mutableListOf<PuzzleEffect>()
        var matched = false
        for (rule in current.level?.rules.orEmpty()) {
            if (current.status == SessionStatus.COMPLETED) break
            if (rule.once && rule.id in current.firedOnceRules) continue
            if (!triggerMatches(rule.on, occurrence) || !rule.conditions.all { evaluate(it, current) }) continue
            matched = true
            if (rule.once) current = current.copy(firedOnceRules = current.firedOnceRules + rule.id)
            for (action in rule.actions) {
                val result = applyAction(action, current)
                current = result.state
                effects += result.effects
                if (current.status == SessionStatus.COMPLETED) break
            }
        }
        return RuleResult(current, effects, matched)
    }

    private fun applyAction(action: Action, current: PuzzleState): ActionResult = when (action) {
        is SetStateAction -> ActionResult(current.copy(variables = current.variables + (action.key to action.value)))
        is IncStateAction -> {
            val value = current.variables[action.key]?.intOrNull ?: 0
            ActionResult(current.copy(variables = current.variables + (action.key to JsonPrimitive(value + action.by))))
        }
        is ShowAction -> ActionResult(current.updateObject(action.target) { it.copy(visible = true) })
        is HideAction -> ActionResult(current.updateObject(action.target) { it.copy(visible = false) })
        is MoveToAction -> ActionResult(
            current.updateObject(action.target) { it.copy(x = action.x, y = action.y) },
            listOf(PuzzleEffect.Animate(action.target, "moveTo", action.ms)),
        )
        is SwapAssetAction -> ActionResult(current.updateObject(action.target) { it.copy(asset = action.asset) })
        is AnimateAction -> ActionResult(current, listOf(PuzzleEffect.Animate(action.target, action.anim.serialValue())))
        is SoundAction -> ActionResult(current, listOf(PuzzleEffect.PlaySound(action.id)))
        is HapticAction -> ActionResult(current, listOf(PuzzleEffect.Haptic(action.id)))
        is FeedbackAction -> ActionResult(current, listOf(PuzzleEffect.Feedback(action.kind)))
        is StartTimerAction -> ActionResult(
            current.copy(activeTimers = current.activeTimers + action.id),
            listOf(PuzzleEffect.ScheduleTimer(action.id, action.ms)),
        )
        is CancelTimerAction -> ActionResult(
            current.copy(activeTimers = current.activeTimers - action.id),
            listOf(PuzzleEffect.CancelTimer(action.id)),
        )
        WinAction -> ActionResult(current.copy(status = SessionStatus.COMPLETED), listOf(PuzzleEffect.LevelCompleted))
    }

    private fun showHint(): SessionResult {
        val nextIndex = (state.hintIndex + 1).coerceAtMost(3)
        val effects = mutableListOf<PuzzleEffect>(PuzzleEffect.ShowHint(nextIndex))
        if (state.level?.hints?.getOrNull(nextIndex - 1)?.showSolution == true) effects += PuzzleEffect.ShowSolution
        return finishInput(state.copy(hintIndex = nextIndex), effects)
    }

    private fun ignored(input: RuntimeInput, reason: String): SessionResult = SessionResult(
        copy(state = state.copy(logs = state.logs + RuntimeLog(input, reason))),
    )
}

private sealed interface Occurrence {
    data class Tap(val target: String) : Occurrence
    data class LongPress(val target: String) : Occurrence
    data class DragEnd(val target: String, val dropOn: String?) : Occurrence
    data class DragOut(val target: String, val edge: Edge) : Occurrence
    data class MultiTouch(val count: Int, val target: String?) : Occurrence
    data class Device(val event: DeviceEvent) : Occurrence
    data class Timer(val id: String) : Occurrence
}

private data class RuleResult(val state: PuzzleState, val effects: List<PuzzleEffect>, val matched: Boolean)
private data class ActionResult(val state: PuzzleState, val effects: List<PuzzleEffect> = emptyList())

private fun Level.toReadyState(): PuzzleState = PuzzleState(
    status = SessionStatus.READY,
    level = this,
    variables = state,
    scene = scene?.let { SceneState(it.width, it.height, it.objects.map(::ObjectState)) },
)

private fun PuzzleState.updateObject(id: String, transform: (ObjectState) -> ObjectState): PuzzleState =
    copy(scene = scene?.update(id, transform))

private fun crossedEdge(scene: SceneState, x: Double, y: Double): Edge? = buildList {
    if (x < 0) add(Edge.LEFT to -x)
    if (x > scene.width) add(Edge.RIGHT to (x - scene.width))
    if (y < 0) add(Edge.TOP to -y)
    if (y > scene.height) add(Edge.BOTTOM to (y - scene.height))
}.maxByOrNull { it.second }?.first

private fun triggerMatches(trigger: Trigger, occurrence: Occurrence): Boolean = when (trigger) {
    is TapTrigger -> occurrence is Occurrence.Tap && trigger.target == occurrence.target
    is LongPressTrigger -> occurrence is Occurrence.LongPress && trigger.target == occurrence.target
    is DragEndTrigger -> occurrence is Occurrence.DragEnd && trigger.target == occurrence.target && when (trigger.dropOn) {
        null -> true
        "any" -> occurrence.dropOn != null
        "none" -> occurrence.dropOn == null
        else -> trigger.dropOn == occurrence.dropOn
    }
    is DragOutTrigger -> occurrence is Occurrence.DragOut && trigger.target == occurrence.target &&
        (trigger.edge == Edge.ANY || trigger.edge == occurrence.edge)
    is MultiTouchTrigger -> occurrence is Occurrence.MultiTouch && occurrence.count >= trigger.count &&
        (trigger.target == null || trigger.target == occurrence.target)
    is DeviceTrigger -> occurrence is Occurrence.Device && trigger.event == occurrence.event
    is TimerTrigger -> occurrence is Occurrence.Timer && trigger.id == occurrence.id
}

internal fun evaluate(condition: Condition, state: PuzzleState): Boolean = when (condition) {
    is StateEqualsCondition -> state.variables[condition.key] == condition.value
    is StateAtLeastCondition -> (state.variables[condition.key]?.intOrNull ?: Int.MIN_VALUE) >= condition.value
    is ObjectVisibleCondition -> state.scene?.objectById(condition.target)?.visible == true
    is ObjectOverlapsCondition -> {
        val first = state.scene?.objectById(condition.a)
        val second = state.scene?.objectById(condition.b)
        first != null && second != null && first.overlaps(second)
    }
    is AllOfCondition -> condition.conditions.all { evaluate(it, state) }
    is AnyOfCondition -> condition.conditions.any { evaluate(it, state) }
    is NotCondition -> !evaluate(condition.condition, state)
}

private fun AnimationKind.serialValue(): String = when (this) {
    AnimationKind.WIGGLE -> "wiggle"
    AnimationKind.POP -> "pop"
    AnimationKind.SPIN -> "spin"
    AnimationKind.BOUNCE -> "bounce"
    AnimationKind.FADE_IN -> "fadeIn"
    AnimationKind.FADE_OUT -> "fadeOut"
}
