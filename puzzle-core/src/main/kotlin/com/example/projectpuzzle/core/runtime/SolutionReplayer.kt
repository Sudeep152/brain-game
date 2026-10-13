package com.example.projectpuzzle.core.runtime

import com.example.projectpuzzle.core.model.*

data class ReplayResult(
    val session: PuzzleSession,
    val effects: List<PuzzleEffect>,
    val error: String? = null,
) {
    val completed: Boolean get() = session.state.status == SessionStatus.COMPLETED
}

object SolutionReplayer {
    fun replay(level: Level, steps: List<SolutionStep>): ReplayResult {
        var session = PuzzleSession.ready(level)
        val effects = mutableListOf<PuzzleEffect>()
        session = session.handle(RuntimeInput.Start).also { effects += it.effects }.session

        for (step in steps) {
            val inputs = when (step) {
                is TapStep -> listOf(RuntimeInput.Tap(step.target))
                is LongPressStep -> listOf(RuntimeInput.LongPress(step.target))
                is MultiTouchStep -> listOf(RuntimeInput.MultiTouch(step.count, step.target))
                is DeviceStep -> listOf(RuntimeInput.Device(step.event))
                is TimerStep -> listOf(RuntimeInput.TimerFired(step.id))
                is DragStep -> {
                    val destination = when {
                        step.toObject != null -> session.state.scene?.objectById(step.toObject)?.let { it.x to it.y }
                        step.x != null && step.y != null -> step.x to step.y
                        else -> null
                    } ?: return ReplayResult(session, effects, "Invalid drag destination for ${step.target}")
                    listOf(RuntimeInput.DragStart(step.target), RuntimeInput.DragEnd(step.target, destination.first, destination.second))
                }
            }
            for (input in inputs) {
                val result = session.handle(input)
                session = result.session
                effects += result.effects
            }
        }
        return ReplayResult(session, effects)
    }
}
