package com.example.projectpuzzle.core

import com.example.projectpuzzle.core.model.*
import com.example.projectpuzzle.core.runtime.*
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PuzzleRuntimeTest {
    @Test
    fun `state machine accepts only legal transitions`() {
        val level = testLevel()
        val empty = PuzzleSession()
        assertEquals(SessionStatus.NOT_LOADED, empty.state.status)
        assertEquals(SessionStatus.LOADING, empty.beginLoading().state.status)
        assertEquals(SessionStatus.ERROR, empty.beginLoading().failLoading("bad").state.status)

        var session = empty.beginLoading().finishLoading(level)
        assertEquals(SessionStatus.READY, session.state.status)
        assertTrue(session.handle(RuntimeInput.Tap("a")).session.state.logs.isNotEmpty())
        session = session.handle(RuntimeInput.Start).session
        assertEquals(SessionStatus.PLAYING, session.state.status)
        session = session.handle(RuntimeInput.Pause).session
        assertEquals(SessionStatus.PAUSED, session.state.status)
        session = session.handle(RuntimeInput.Resume).session
        assertEquals(SessionStatus.PLAYING, session.state.status)
        session = session.handle(RuntimeInput.Tap("a")).session
        assertEquals(SessionStatus.COMPLETED, session.state.status)
        assertTrue(session.handle(RuntimeInput.Tap("a")).session.state.logs.isNotEmpty())
    }

    @Test
    fun `every trigger type fires`() {
        val increment = listOf(IncStateAction("hits"))
        val rules = listOf(
            Rule("tap", TapTrigger("a"), actions = increment),
            Rule("long", LongPressTrigger("a"), actions = increment),
            Rule("drop", DragEndTrigger("a", "b"), actions = increment),
            Rule("out", DragOutTrigger("a", Edge.RIGHT), actions = increment),
            Rule("multi", MultiTouchTrigger(2, "a"), actions = increment),
            Rule("device", DeviceTrigger(DeviceEvent.SHAKE), actions = increment),
            Rule("start-timer", TapTrigger("b"), actions = listOf(StartTimerAction("clock", 10))),
            Rule("timer", TimerTrigger("clock"), actions = increment),
        )
        var session = started(testLevel(rules = rules, solution = emptyList()))
        session = session.handle(RuntimeInput.Tap("a")).session
        session = session.handle(RuntimeInput.LongPress("a")).session
        session = drag(session, "a", 700.0, 500.0)
        session = drag(session, "a", 1100.0, 500.0)
        session = session.handle(RuntimeInput.MultiTouch(2, "a")).session
        session = session.handle(RuntimeInput.Device(DeviceEvent.SHAKE)).session
        session = session.handle(RuntimeInput.Tap("b")).session
        session = session.handle(RuntimeInput.TimerFired("clock")).session
        assertEquals(7, session.state.variables.getValue("hits").content.toInt())
    }

    @Test
    fun `every condition type evaluates`() {
        val objects = listOf(
            testObject("a", x = 300.0),
            testObject("b", x = 340.0),
            testObject("hidden", visible = false),
        )
        val conditions = listOf<Condition>(
            StateEqualsCondition("flag", JsonPrimitive(true)),
            StateAtLeastCondition("count", 2),
            ObjectVisibleCondition("a"),
            ObjectOverlapsCondition("a", "b"),
            AllOfCondition(listOf(ObjectVisibleCondition("a"), StateAtLeastCondition("count", 1))),
            AnyOfCondition(listOf(ObjectVisibleCondition("hidden"), ObjectVisibleCondition("a"))),
            NotCondition(ObjectVisibleCondition("hidden")),
        )
        val rules = conditions.mapIndexed { index, condition ->
            Rule("condition-$index", TapTrigger("a"), conditions = listOf(condition), actions = listOf(IncStateAction("hits")))
        }
        val level = testLevel(
            state = mapOf("flag" to JsonPrimitive(true), "count" to JsonPrimitive(2), "hits" to JsonPrimitive(0)),
            objects = objects,
            rules = rules,
            solution = emptyList(),
        )
        val session = started(level).handle(RuntimeInput.Tap("a")).session
        assertEquals(7, session.state.variables.getValue("hits").content.toInt())
    }

    @Test
    fun `every action type applies in order`() {
        val actions = listOf<Action>(
            SetStateAction("flag", JsonPrimitive(true)),
            IncStateAction("count", 2),
            ShowAction("hidden"),
            HideAction("b"),
            MoveToAction("a", 400.0, 600.0, 25),
            SwapAssetAction("a", "obj.other"),
            AnimateAction("a", AnimationKind.POP),
            SoundAction("sfx.pop"),
            HapticAction(HapticKind.SUCCESS),
            FeedbackAction(FeedbackKind.YES),
            StartTimerAction("clock", 100),
            CancelTimerAction("clock"),
            WinAction,
        )
        val level = testLevel(rules = listOf(Rule("all-actions", TapTrigger("a"), actions = actions)))
        val result = started(level).handle(RuntimeInput.Tap("a"))
        assertEquals(SessionStatus.COMPLETED, result.session.state.status)
        assertEquals(true, result.session.state.variables.getValue("flag").content.toBoolean())
        assertEquals(2, result.session.state.variables.getValue("count").content.toInt())
        assertTrue(result.session.state.scene!!.objectById("hidden")!!.visible)
        assertFalse(result.session.state.scene.objectById("b")!!.visible)
        assertEquals(400.0, result.session.state.scene.objectById("a")!!.x)
        assertEquals("obj.other", result.session.state.scene.objectById("a")!!.asset)
        assertTrue("clock" !in result.session.state.activeTimers)
        assertTrue(result.effects.any { it is PuzzleEffect.PlaySound })
        assertTrue(result.effects.any { it is PuzzleEffect.Haptic })
        assertTrue(result.effects.any { it is PuzzleEffect.Feedback })
        assertTrue(result.effects.any { it is PuzzleEffect.ScheduleTimer })
        assertTrue(result.effects.any { it is PuzzleEffect.CancelTimer })
        assertTrue(result.effects.any { it is PuzzleEffect.LevelCompleted })
    }

    @Test
    fun `drag snap back hit testing rule order and once follow schema`() {
        val objects = listOf(
            testObject("a", x = 200.0, snapBack = true, z = 0),
            testObject("b", x = 500.0, z = 1),
            testObject("top", x = 500.0, z = 1),
        )
        val rules = listOf(
            Rule("first", TapTrigger("a"), actions = listOf(SetStateAction("flag", JsonPrimitive(true))), once = true),
            Rule(
                "second",
                TapTrigger("a"),
                conditions = listOf(StateEqualsCondition("flag", JsonPrimitive(true))),
                actions = listOf(IncStateAction("hits")),
            ),
        )
        var session = started(testLevel(objects = objects, rules = rules, solution = emptyList()))
        assertEquals("top", session.state.scene!!.hitTest(500.0, 500.0)!!.definition.id)
        session = drag(session, "a", 800.0, 1000.0)
        assertEquals(200.0, session.state.scene!!.objectById("a")!!.x)
        session = session.handle(RuntimeInput.Tap("a")).session
        session = session.handle(RuntimeInput.Tap("a")).session
        assertEquals(2, session.state.variables.getValue("hits").content.toInt())
        assertEquals(setOf("first"), session.state.firedOnceRules)
    }

    @Test
    fun `hints and timers are deterministic`() {
        val rules = listOf(
            Rule("start", TapTrigger("a"), actions = listOf(StartTimerAction("clock", 50))),
            Rule("fire", TimerTrigger("clock"), actions = listOf(WinAction)),
        )
        var session = started(testLevel(rules = rules))
        repeat(3) { index ->
            val result = session.handle(RuntimeInput.HintRequested)
            session = result.session
            assertEquals(index + 1, session.state.hintIndex)
            assertIs<PuzzleEffect.ShowHint>(result.effects.first())
            if (index == 2) assertTrue(result.effects.contains(PuzzleEffect.ShowSolution))
        }
        val unknown = session.handle(RuntimeInput.TimerFired("clock"))
        assertEquals(SessionStatus.PLAYING, unknown.session.state.status)
        session = session.handle(RuntimeInput.Tap("a")).session
        assertTrue("clock" in session.state.activeTimers)
        val fired = session.handle(RuntimeInput.TimerFired("clock"))
        assertEquals(SessionStatus.COMPLETED, fired.session.state.status)
    }

    @Test
    fun `declarative win is checked after drag movement`() {
        val level = testLevel(
            objects = listOf(testObject("a", x = 200.0), testObject("b", x = 700.0)),
            rules = emptyList(),
            win = ObjectOverlapsCondition("a", "b"),
            solution = emptyList(),
        )
        var session = started(level)
        session = session.handle(RuntimeInput.DragStart("a")).session
        val result = session.handle(RuntimeInput.DragMove("a", 700.0, 500.0))
        assertEquals(SessionStatus.COMPLETED, result.session.state.status)
        assertTrue(result.effects.contains(PuzzleEffect.LevelCompleted))
    }

    private fun started(level: Level): PuzzleSession = PuzzleSession.ready(level).handle(RuntimeInput.Start).session

    private fun drag(session: PuzzleSession, id: String, x: Double, y: Double): PuzzleSession =
        session.handle(RuntimeInput.DragStart(id)).session.handle(RuntimeInput.DragEnd(id, x, y)).session
}
