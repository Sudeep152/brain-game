package com.example.projectpuzzle.core

import com.example.projectpuzzle.core.model.*
import com.example.projectpuzzle.core.validation.LevelValidator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelValidatorTest {
    @Test
    fun `valid level has no issues`() {
        assertEquals(emptyList(), LevelValidator.validate(testLevel(), validationContext()))
    }

    @Test
    fun `strict parser reports malformed missing unknown and discriminator errors`() {
        assertHas("PARSE_ERROR", LevelValidator.validate("{", validationContext()))

        val encoded = LevelValidator.strictJson.encodeToString(testLevel())
        assertHas("FIELD_NOT_ALLOWED", LevelValidator.validate(encoded.replaceFirst("{", "{\"extra\":true,"), validationContext()))
        assertHas("MISSING_FIELD", LevelValidator.validate(encoded.replace("\"schemaVersion\":1,", ""), validationContext()))
        assertHas("INVALID_VALUE", LevelValidator.validate(encoded.replace("\"type\":\"tap\"", "\"type\":\"unknown\""), validationContext()))
    }

    @Test
    fun `validator emits every required semantic issue code`() {
        val valid = testLevel()
        val scene = requireNotNull(valid.scene)
        val cases = mapOf(
            "UNSUPPORTED_SCHEMA" to valid.copy(schemaVersion = 99),
            "UNSUPPORTED_RUNTIME" to valid.copy(runtime = "missing"),
            "APP_TOO_OLD" to valid.copy(minAppVersion = 2),
            "MISSING_FIELD" to valid.copy(scene = null),
            "FIELD_NOT_ALLOWED" to valid.copy(params = JsonObject(mapOf("x" to JsonPrimitive(1)))),
            "DUPLICATE_ID" to valid.copy(scene = scene.copy(objects = scene.objects + scene.objects.first())),
            "UNKNOWN_REFERENCE" to valid.copy(rules = listOf(Rule("bad", TapTrigger("missing"), actions = listOf(WinAction)))),
            "UNKNOWN_ASSET" to valid.copy(scene = scene.copy(background = "missing")),
            "WRONG_ASSET_KIND" to valid.copy(scene = scene.copy(background = "sfx.pop")),
            "MISSING_EN_LOCALE" to valid.copy(locales = listOf("hi")),
            "OUT_OF_BOUNDS" to valid.copy(scene = scene.copy(objects = scene.objects.mapIndexed { index, obj -> if (index == 0) obj.copy(x = -10.0) else obj })),
            "INVALID_VALUE" to valid.copy(difficulty = 0),
            "TYPE_MISMATCH" to valid.copy(rules = listOf(Rule("bad-type", TapTrigger("a"), actions = listOf(SetStateAction("flag", JsonPrimitive(1)), WinAction)))),
            "NOT_WINNABLE" to valid.copy(rules = listOf(Rule("no-win", TapTrigger("a"), actions = listOf(FeedbackAction(FeedbackKind.NOPE))))),
            "HINT_COUNT" to valid.copy(hints = valid.hints.take(2)),
            "SOLUTION_FAILED" to valid.copy(solution = emptyList()),
        )
        for ((code, level) in cases) assertHas(code, LevelValidator.validate(level, validationContext()))
        assertHas("MISSING_TEXT", LevelValidator.validate(valid, validationContext(strings = emptyMap())))
    }

    @Test
    fun `family params receive shape validation without solver`() {
        val matchstick = testLevel().copy(
            family = LevelFamily.MATCHSTICK,
            scene = null,
            rules = null,
            solution = null,
            params = JsonObject(mapOf(
                "equation" to JsonPrimitive(""),
                "moves" to JsonPrimitive(4),
                "allowedOps" to kotlinx.serialization.json.JsonArray(listOf(JsonPrimitive("/"))),
            )),
        )
        assertHas("INVALID_VALUE", LevelValidator.validate(matchstick, validationContext()))
        assertTrue(LevelValidator.validate(matchstick, validationContext()).none { it.code == "SOLUTION_FAILED" })
    }

    private fun assertHas(code: String, issues: List<com.example.projectpuzzle.core.validation.ValidationIssue>) {
        assertTrue(issues.any { it.code == code }, "Expected $code, got $issues")
    }
}
