package com.example.projectpuzzle.core

import com.example.projectpuzzle.core.model.*
import com.example.projectpuzzle.core.validation.LevelValidator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class SchemaEncodingTest {
    @Test
    fun `all polymorphic schema types round trip with type discriminators`() {
        val allConditions = listOf<Condition>(
            StateEqualsCondition("flag", JsonPrimitive(false)),
            StateAtLeastCondition("count", 0),
            ObjectVisibleCondition("a"),
            ObjectOverlapsCondition("a", "b"),
            AllOfCondition(listOf(ObjectVisibleCondition("a"))),
            AnyOfCondition(listOf(ObjectVisibleCondition("a"))),
            NotCondition(ObjectVisibleCondition("hidden")),
        )
        val allActions = listOf<Action>(
            SetStateAction("flag", JsonPrimitive(true)),
            IncStateAction("count"),
            ShowAction("hidden"),
            HideAction("hidden"),
            MoveToAction("a", 10.0, 20.0),
            SwapAssetAction("a", "obj.other"),
            AnimateAction("a", AnimationKind.WIGGLE),
            SoundAction("sfx.pop"),
            HapticAction(HapticKind.LIGHT),
            FeedbackAction(FeedbackKind.CLOSE),
            StartTimerAction("clock", 100),
            CancelTimerAction("clock"),
            WinAction,
        )
        val triggers = listOf<Trigger>(
            TapTrigger("a"),
            LongPressTrigger("a"),
            DragEndTrigger("a", "b"),
            DragOutTrigger("a", Edge.ANY),
            MultiTouchTrigger(2, "a"),
            DeviceTrigger(DeviceEvent.SHAKE),
            TimerTrigger("clock"),
        )
        val level = testLevel(
            rules = triggers.mapIndexed { index, trigger ->
                Rule(
                    id = "rule-$index",
                    on = trigger,
                    conditions = if (index == 0) allConditions else emptyList(),
                    actions = if (index == 0) allActions else listOf(FeedbackAction(FeedbackKind.YES)),
                )
            },
            solution = listOf(
                TapStep("a"),
                LongPressStep("a"),
                DragStep("a", toObject = "b"),
                MultiTouchStep(2, "a"),
                DeviceStep(DeviceEvent.SHAKE),
                TimerStep("clock"),
            ),
        )
        val encoded = LevelValidator.strictJson.encodeToString(level)
        assertEquals(level, LevelValidator.strictJson.decodeFromString(encoded))
    }
}
