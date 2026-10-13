package com.example.projectpuzzle.core

import com.example.projectpuzzle.core.model.*
import com.example.projectpuzzle.core.validation.ValidationContext
import kotlinx.serialization.json.JsonPrimitive

internal fun testObject(
    id: String,
    x: Double = 300.0,
    y: Double = 500.0,
    visible: Boolean = true,
    tappable: Boolean = true,
    draggable: Boolean = true,
    snapBack: Boolean = false,
    z: Int = 0,
): SceneObject = SceneObject(
    id = id,
    kind = ObjectKind.SPRITE,
    asset = "obj.test",
    x = x,
    y = y,
    w = 100.0,
    h = 100.0,
    visible = visible,
    tappable = tappable,
    draggable = draggable,
    snapBack = snapBack,
    z = z,
)

internal fun testLevel(
    id: String = "test-level",
    state: Map<String, JsonPrimitive> = mapOf(
        "flag" to JsonPrimitive(false),
        "count" to JsonPrimitive(0),
        "hits" to JsonPrimitive(0),
    ),
    objects: List<SceneObject> = listOf(
        testObject("a"),
        testObject("b", x = 700.0, y = 500.0),
        testObject("hidden", visible = false),
    ),
    rules: List<Rule> = listOf(Rule("win", TapTrigger("a"), actions = listOf(WinAction))),
    win: Condition? = null,
    solution: List<SolutionStep> = listOf(TapStep("a")),
): Level = Level(
    schemaVersion = 1,
    id = id,
    family = LevelFamily.TEASER,
    runtime = "native-v1",
    minAppVersion = 1,
    difficulty = 1,
    languageDependent = false,
    locales = listOf("en", "hi"),
    text = LevelText("prompt"),
    hints = listOf(Hint("h1"), Hint("h2"), Hint("h3", showSolution = true)),
    state = state,
    scene = Scene(1000, 1600, "bg.paper", objects),
    rules = rules,
    win = win,
    solution = solution,
)

internal fun validationContext(
    strings: Map<String, Set<String>> = mapOf(
        "en" to setOf("prompt", "h1", "h2", "h3"),
        "hi" to setOf("prompt", "h1", "h2", "h3"),
    ),
): ValidationContext = ValidationContext(
    supportedSchemaVersions = setOf(1),
    supportedRuntimes = setOf("native-v1"),
    appVersion = 1,
    assets = mapOf(
        "bg.paper" to AssetKind.BACKGROUND,
        "obj.test" to AssetKind.SPRITE,
        "obj.other" to AssetKind.SPRITE,
        "sfx.pop" to AssetKind.SOUND,
    ),
    strings = strings,
)
