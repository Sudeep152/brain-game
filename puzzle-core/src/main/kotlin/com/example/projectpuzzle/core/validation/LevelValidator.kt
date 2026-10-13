package com.example.projectpuzzle.core.validation

import com.example.projectpuzzle.core.model.*
import com.example.projectpuzzle.core.runtime.SolutionReplayer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

data class ValidationContext(
    val supportedSchemaVersions: Set<Int>,
    val supportedRuntimes: Set<String>,
    val appVersion: Int,
    val assets: Map<String, AssetKind>,
    val strings: Map<String, Set<String>>,
)

data class ValidationIssue(val code: String, val path: String, val message: String)

object LevelValidator {
    val strictJson = Json {
        ignoreUnknownKeys = false
        classDiscriminator = "type"
        explicitNulls = false
    }

    fun decode(levelJson: String): Level = strictJson.decodeFromString(levelJson)

    fun validate(levelJson: String, ctx: ValidationContext): List<ValidationIssue> = try {
        val level = try {
            decode(levelJson)
        } catch (error: SerializationException) {
            return listOf(parseIssue(error))
        } catch (error: IllegalArgumentException) {
            return listOf(ValidationIssue("PARSE_ERROR", "/", error.message ?: "Invalid JSON"))
        }
        validate(level, ctx)
    } catch (error: Exception) {
        listOf(ValidationIssue("PARSE_ERROR", "/", error.message ?: "Validation failed"))
    }

    fun validate(level: Level, ctx: ValidationContext): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()
        fun issue(code: String, path: String, message: String) {
            issues += ValidationIssue(code, path, message)
        }

        if (level.schemaVersion !in ctx.supportedSchemaVersions) {
            issue("UNSUPPORTED_SCHEMA", "/schemaVersion", "Unsupported schema ${level.schemaVersion}")
        }
        if (level.runtime !in ctx.supportedRuntimes) {
            issue("UNSUPPORTED_RUNTIME", "/runtime", "Unsupported runtime ${level.runtime}")
        }
        if (level.minAppVersion > ctx.appVersion) {
            issue("APP_TOO_OLD", "/minAppVersion", "Requires app version ${level.minAppVersion}")
        }
        if (!ID_PATTERN.matches(level.id)) issue("INVALID_VALUE", "/id", "Invalid level id")
        if (level.difficulty !in 1..5) issue("INVALID_VALUE", "/difficulty", "Difficulty must be 1..5")
        if ("en" !in level.locales) issue("MISSING_EN_LOCALE", "/locales", "English locale is required")
        if (level.locales.isEmpty() || level.locales.distinct().size != level.locales.size) {
            issue("INVALID_VALUE", "/locales", "Locales must be non-empty and unique")
        }
        if (level.hints.size != 3) issue("HINT_COUNT", "/hints", "Exactly 3 hints are required")

        val scene = level.scene
        val rules = level.rules
        val objectIds = scene?.objects?.map { it.id }?.toSet().orEmpty()
        val stateKeys = level.state.keys
        val timerIds = rules.orEmpty().flatMap { rule ->
            rule.actions.mapNotNull { (it as? StartTimerAction)?.id }
        }.toSet()

        level.state.forEach { (key, value) ->
            if (!ID_PATTERN.matches(key)) issue("INVALID_VALUE", "/state/$key", "Invalid state key")
            if (stateType(value) == null) issue("INVALID_VALUE", "/state/$key", "State must be boolean, integer, or string")
        }

        val textKeys = mutableListOf<Pair<String, String>>()
        textKeys += "/text/prompt" to level.text.prompt
        level.hints.forEachIndexed { index, hint ->
            textKeys += "/hints/$index/textKey" to hint.textKey
            if (hint.highlight != null && hint.highlight !in objectIds) {
                issue("UNKNOWN_REFERENCE", "/hints/$index/highlight", "Unknown object ${hint.highlight}")
            }
            if (hint.showSolution && index != 2) {
                issue("INVALID_VALUE", "/hints/$index/showSolution", "Only the third hint may show the solution")
            }
        }

        when (level.family) {
            LevelFamily.TEASER, LevelFamily.COMBINE -> {
                if (scene == null) issue("MISSING_FIELD", "/scene", "Scene is required")
                if (rules.isNullOrEmpty()) issue("MISSING_FIELD", "/rules", "Non-empty rules are required")
                if (level.solution == null) issue("MISSING_FIELD", "/solution", "Solution is required")
                if (level.params != null) issue("FIELD_NOT_ALLOWED", "/params", "Params are not allowed for this family")
                if (rules.orEmpty().none { rule -> rule.actions.any { it == WinAction } } && level.win == null) {
                    issue("NOT_WINNABLE", "/", "Level needs a win action or condition")
                }
            }
            LevelFamily.MATCHSTICK, LevelFamily.ODD_ONE_OUT -> {
                if (level.params == null) issue("MISSING_FIELD", "/params", "Params are required")
                if (scene != null) issue("FIELD_NOT_ALLOWED", "/scene", "Scene is not allowed for this family")
                if (rules != null) issue("FIELD_NOT_ALLOWED", "/rules", "Rules are not allowed for this family")
                if (level.win != null) issue("FIELD_NOT_ALLOWED", "/win", "Win is not allowed for this family")
                if (level.solution != null) issue("FIELD_NOT_ALLOWED", "/solution", "Solution is not allowed for this family")
            }
        }

        scene?.let { validateScene(it, ctx, textKeys, issues) }
        rules.orEmpty().let { validateRules(it, objectIds, stateKeys, timerIds, ctx, level.state, issues) }
        level.win?.let { validateCondition(it, "/win", objectIds, stateKeys, level.state, issues) }
        validateSolutions(level, objectIds, timerIds, issues)
        validateParams(level, issues)

        for ((path, key) in textKeys) {
            for (locale in level.locales) {
                if (key !in ctx.strings[locale].orEmpty()) {
                    issue("MISSING_TEXT", path, "Missing '$key' for locale '$locale'")
                }
            }
        }

        if (issues.isEmpty() && level.solution != null) {
            val solutions = listOf(level.solution) + level.altSolutions
            solutions.forEachIndexed { index, steps ->
                val replay = SolutionReplayer.replay(level, steps)
                if (!replay.completed) {
                    val path = if (index == 0) "/solution" else "/altSolutions/${index - 1}"
                    issue("SOLUTION_FAILED", path, replay.error ?: "Solution did not complete the level")
                }
            }
        }
        return issues
    }

    private fun validateScene(
        scene: Scene,
        ctx: ValidationContext,
        textKeys: MutableList<Pair<String, String>>,
        issues: MutableList<ValidationIssue>,
    ) {
        fun issue(code: String, path: String, message: String) {
            issues += ValidationIssue(code, path, message)
        }
        if (scene.width != 1000 || scene.height != 1600) {
            issue("INVALID_VALUE", "/scene", "Scene must be exactly 1000 x 1600")
        }
        validateAsset(scene.background, AssetKind.BACKGROUND, "/scene/background", ctx, issues)
        val seen = mutableSetOf<String>()
        scene.objects.forEachIndexed { index, obj ->
            val path = "/scene/objects/$index"
            if (!ID_PATTERN.matches(obj.id)) issue("INVALID_VALUE", "$path/id", "Invalid object id")
            if (!seen.add(obj.id)) issue("DUPLICATE_ID", "$path/id", "Duplicate object id ${obj.id}")
            if (obj.w <= 0 || obj.h <= 0) issue("INVALID_VALUE", path, "Object dimensions must be positive")
            if (obj.x - obj.w / 2 < 0 || obj.x + obj.w / 2 > scene.width ||
                obj.y - obj.h / 2 < 0 || obj.y + obj.h / 2 > scene.height
            ) issue("OUT_OF_BOUNDS", path, "Object bounds exceed scene")
            when (obj.kind) {
                ObjectKind.SPRITE -> {
                    if (obj.asset == null) issue("MISSING_FIELD", "$path/asset", "Sprite asset is required")
                    else validateAsset(obj.asset, AssetKind.SPRITE, "$path/asset", ctx, issues)
                    if (obj.text != null || obj.textKey != null || obj.shape != null || obj.color != null) {
                        issue("FIELD_NOT_ALLOWED", path, "Sprite has fields for another object kind")
                    }
                }
                ObjectKind.TEXT -> {
                    if ((obj.text == null) == (obj.textKey == null)) {
                        issue("INVALID_VALUE", path, "Text object requires exactly one of text or textKey")
                    }
                    obj.textKey?.let { textKeys += "$path/textKey" to it }
                    obj.text?.let {
                        val codePoint = if (it.codePointCount(0, it.length) == 1) it.codePointAt(0) else null
                        val literalAllowed = MATH_LITERAL.matches(it) ||
                            (codePoint != null && Character.getType(codePoint) == Character.OTHER_SYMBOL.toInt())
                        if (!literalAllowed) issue("INVALID_VALUE", "$path/text", "Literal text must be digits, math, or one emoji")
                    }
                    if (obj.asset != null || obj.shape != null || obj.color != null) {
                        issue("FIELD_NOT_ALLOWED", path, "Text has fields for another object kind")
                    }
                }
                ObjectKind.SHAPE -> {
                    if (obj.shape == null) issue("MISSING_FIELD", "$path/shape", "Shape is required")
                    if (obj.color.isNullOrBlank()) issue("MISSING_FIELD", "$path/color", "Color token is required")
                    if (obj.asset != null || obj.text != null || obj.textKey != null) {
                        issue("FIELD_NOT_ALLOWED", path, "Shape has fields for another object kind")
                    }
                }
            }
        }
    }

    private fun validateRules(
        rules: List<Rule>,
        objectIds: Set<String>,
        stateKeys: Set<String>,
        timerIds: Set<String>,
        ctx: ValidationContext,
        initialState: Map<String, JsonPrimitive>,
        issues: MutableList<ValidationIssue>,
    ) {
        val seen = mutableSetOf<String>()
        rules.forEachIndexed { index, rule ->
            val path = "/rules/$index"
            if (!ID_PATTERN.matches(rule.id)) issues += ValidationIssue("INVALID_VALUE", "$path/id", "Invalid rule id")
            if (!seen.add(rule.id)) issues += ValidationIssue("DUPLICATE_ID", "$path/id", "Duplicate rule id ${rule.id}")
            if (rule.actions.isEmpty()) issues += ValidationIssue("INVALID_VALUE", "$path/do", "Actions cannot be empty")
            validateTrigger(rule.on, "$path/on", objectIds, timerIds, issues)
            rule.conditions.forEachIndexed { conditionIndex, condition ->
                validateCondition(condition, "$path/if/$conditionIndex", objectIds, stateKeys, initialState, issues)
            }
            rule.actions.forEachIndexed { actionIndex, action ->
                validateAction(action, "$path/do/$actionIndex", objectIds, stateKeys, timerIds, ctx, initialState, issues)
            }
        }
    }

    private fun validateTrigger(
        trigger: Trigger,
        path: String,
        objects: Set<String>,
        timers: Set<String>,
        issues: MutableList<ValidationIssue>,
    ) {
        fun objectRef(id: String, suffix: String = "target") {
            if (id !in objects) issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/$suffix", "Unknown object $id")
        }
        when (trigger) {
            is TapTrigger -> objectRef(trigger.target)
            is LongPressTrigger -> objectRef(trigger.target)
            is DragEndTrigger -> {
                objectRef(trigger.target)
                trigger.dropOn?.takeUnless { it == "any" || it == "none" }?.let { objectRef(it, "dropOn") }
            }
            is DragOutTrigger -> objectRef(trigger.target)
            is MultiTouchTrigger -> {
                if (trigger.count !in 2..5) issues += ValidationIssue("INVALID_VALUE", "$path/count", "Count must be 2..5")
                trigger.target?.let(::objectRef)
            }
            is DeviceTrigger -> Unit
            is TimerTrigger -> if (trigger.id !in timers) {
                issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/id", "Unknown timer ${trigger.id}")
            }
        }
    }

    private fun validateCondition(
        condition: Condition,
        path: String,
        objects: Set<String>,
        stateKeys: Set<String>,
        initialState: Map<String, JsonPrimitive>,
        issues: MutableList<ValidationIssue>,
    ) {
        fun stateRef(key: String) {
            if (key !in stateKeys) issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/key", "Unknown state key $key")
        }
        fun objectRef(id: String, field: String) {
            if (id !in objects) issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/$field", "Unknown object $id")
        }
        when (condition) {
            is StateEqualsCondition -> {
                stateRef(condition.key)
                if (condition.key in stateKeys && stateType(initialState.getValue(condition.key)) != stateType(condition.value)) {
                    issues += ValidationIssue("TYPE_MISMATCH", "$path/value", "State value type does not match")
                }
            }
            is StateAtLeastCondition -> {
                stateRef(condition.key)
                if (condition.key in stateKeys && initialState.getValue(condition.key).intOrNull == null) {
                    issues += ValidationIssue("TYPE_MISMATCH", "$path/key", "State must be an integer")
                }
            }
            is ObjectVisibleCondition -> objectRef(condition.target, "target")
            is ObjectOverlapsCondition -> {
                objectRef(condition.a, "a")
                objectRef(condition.b, "b")
            }
            is AllOfCondition -> {
                if (condition.conditions.isEmpty()) issues += ValidationIssue("INVALID_VALUE", "$path/conditions", "Conditions cannot be empty")
                condition.conditions.forEachIndexed { i, nested -> validateCondition(nested, "$path/conditions/$i", objects, stateKeys, initialState, issues) }
            }
            is AnyOfCondition -> {
                if (condition.conditions.isEmpty()) issues += ValidationIssue("INVALID_VALUE", "$path/conditions", "Conditions cannot be empty")
                condition.conditions.forEachIndexed { i, nested -> validateCondition(nested, "$path/conditions/$i", objects, stateKeys, initialState, issues) }
            }
            is NotCondition -> validateCondition(condition.condition, "$path/condition", objects, stateKeys, initialState, issues)
        }
    }

    private fun validateAction(
        action: Action,
        path: String,
        objects: Set<String>,
        stateKeys: Set<String>,
        timers: Set<String>,
        ctx: ValidationContext,
        initialState: Map<String, JsonPrimitive>,
        issues: MutableList<ValidationIssue>,
    ) {
        fun objectRef(id: String) {
            if (id !in objects) issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/target", "Unknown object $id")
        }
        fun stateRef(key: String) {
            if (key !in stateKeys) issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/key", "Unknown state key $key")
        }
        when (action) {
            is SetStateAction -> {
                stateRef(action.key)
                if (action.key in stateKeys && stateType(initialState.getValue(action.key)) != stateType(action.value)) {
                    issues += ValidationIssue("TYPE_MISMATCH", "$path/value", "State value type does not match")
                }
            }
            is IncStateAction -> {
                stateRef(action.key)
                if (action.key in stateKeys && initialState.getValue(action.key).intOrNull == null) {
                    issues += ValidationIssue("TYPE_MISMATCH", "$path/key", "State must be an integer")
                }
            }
            is ShowAction -> objectRef(action.target)
            is HideAction -> objectRef(action.target)
            is MoveToAction -> {
                objectRef(action.target)
                if (action.ms < 0) issues += ValidationIssue("INVALID_VALUE", "$path/ms", "Duration cannot be negative")
            }
            is SwapAssetAction -> {
                objectRef(action.target)
                validateAsset(action.asset, AssetKind.SPRITE, "$path/asset", ctx, issues)
            }
            is AnimateAction -> objectRef(action.target)
            is SoundAction -> validateAsset(action.id, AssetKind.SOUND, "$path/id", ctx, issues)
            is HapticAction, is FeedbackAction, WinAction -> Unit
            is StartTimerAction -> if (action.ms <= 0) issues += ValidationIssue("INVALID_VALUE", "$path/ms", "Timer duration must be positive")
            is CancelTimerAction -> if (action.id !in timers) {
                issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/id", "Unknown timer ${action.id}")
            }
        }
    }

    private fun validateSolutions(
        level: Level,
        objects: Set<String>,
        timers: Set<String>,
        issues: MutableList<ValidationIssue>,
    ) {
        val groups = level.solution?.let { listOf(it) + level.altSolutions }.orEmpty()
        groups.forEachIndexed { groupIndex, steps ->
            val base = if (groupIndex == 0) "/solution" else "/altSolutions/${groupIndex - 1}"
            steps.forEachIndexed { index, step ->
                val path = "$base/$index"
                fun objectRef(id: String, field: String = "target") {
                    if (id !in objects) issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/$field", "Unknown object $id")
                }
                when (step) {
                    is TapStep -> objectRef(step.target)
                    is LongPressStep -> objectRef(step.target)
                    is DragStep -> {
                        objectRef(step.target)
                        val objectDestination = step.toObject != null
                        val coordinateDestination = step.x != null && step.y != null
                        if (objectDestination == coordinateDestination) {
                            issues += ValidationIssue("INVALID_VALUE", path, "Drag needs exactly one destination form")
                        }
                        step.toObject?.let { objectRef(it, "toObject") }
                    }
                    is MultiTouchStep -> if (step.count !in 2..5) {
                        issues += ValidationIssue("INVALID_VALUE", "$path/count", "Count must be 2..5")
                    } else step.target?.let(::objectRef)
                    is DeviceStep -> Unit
                    is TimerStep -> if (step.id !in timers) {
                        issues += ValidationIssue("UNKNOWN_REFERENCE", "$path/id", "Unknown timer ${step.id}")
                    }
                }
            }
        }
    }

    private fun validateParams(level: Level, issues: MutableList<ValidationIssue>) {
        val params = level.params ?: return
        try {
            when (level.family) {
                LevelFamily.MATCHSTICK -> {
                    val value = strictJson.decodeFromJsonElement<MatchstickParams>(params)
                    if (value.equation.isBlank()) issues += ValidationIssue("INVALID_VALUE", "/params/equation", "Equation is required")
                    if (value.moves !in 1..3) issues += ValidationIssue("INVALID_VALUE", "/params/moves", "Moves must be 1..3")
                    if (value.allowedOps.isEmpty() || value.allowedOps.any { it !in setOf("+", "-", "x", "=") }) {
                        issues += ValidationIssue("INVALID_VALUE", "/params/allowedOps", "Invalid allowed operations")
                    }
                }
                LevelFamily.ODD_ONE_OUT -> {
                    val value = strictJson.decodeFromJsonElement<OddOneOutParams>(params)
                    if (value.rows !in 2..10) issues += ValidationIssue("INVALID_VALUE", "/params/rows", "Rows must be 2..10")
                    if (value.cols !in 2..10) issues += ValidationIssue("INVALID_VALUE", "/params/cols", "Columns must be 2..10")
                    if (value.base.isBlank()) issues += ValidationIssue("INVALID_VALUE", "/params/base", "Base is required")
                    if (value.variation.delta <= 0) issues += ValidationIssue("INVALID_VALUE", "/params/variation/delta", "Delta must be positive")
                }
                else -> Unit
            }
        } catch (error: SerializationException) {
            issues += parseIssue(error).copy(path = "/params")
        }
    }

    private fun validateAsset(
        id: String,
        expected: AssetKind,
        path: String,
        ctx: ValidationContext,
        issues: MutableList<ValidationIssue>,
    ) {
        val actual = ctx.assets[id]
        when {
            actual == null -> issues += ValidationIssue("UNKNOWN_ASSET", path, "Unknown asset $id")
            actual != expected -> issues += ValidationIssue("WRONG_ASSET_KIND", path, "Expected $expected but found $actual")
        }
    }

    private fun parseIssue(error: SerializationException): ValidationIssue {
        val message = error.message ?: "Invalid JSON"
        val code = when {
            "Encountered an unknown key" in message -> "FIELD_NOT_ALLOWED"
            "is required for type" in message || "Field '" in message && "required" in message -> "MISSING_FIELD"
            "Serializer for subclass" in message || "does not contain element with name" in message -> "INVALID_VALUE"
            else -> "PARSE_ERROR"
        }
        return ValidationIssue(code, "/", message)
    }

    private fun stateType(value: JsonPrimitive): StateType? = when {
        value.isString -> StateType.STRING
        value.booleanOrNull != null -> StateType.BOOLEAN
        value.longOrNull != null -> StateType.INTEGER
        else -> null
    }

    private enum class StateType { BOOLEAN, INTEGER, STRING }

    private val ID_PATTERN = Regex("[a-z0-9][a-z0-9._-]*")
    private val MATH_LITERAL = Regex("[0-9+\\-×x=÷*/(). ]+")
}
