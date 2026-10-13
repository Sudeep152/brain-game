package com.example.projectpuzzle.core

import com.example.projectpuzzle.core.model.AssetCatalog
import com.example.projectpuzzle.core.runtime.SolutionReplayer
import com.example.projectpuzzle.core.validation.LevelValidator
import com.example.projectpuzzle.core.validation.ValidationContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class ContentReplayTest {
    @Test
    fun `every bundled level validates and every solution completes`() {
        val content = Path.of("../app/src/main/assets/content").normalize()
        val catalog = LevelValidator.strictJson.decodeFromString<AssetCatalog>(content.resolve("assets.json").readText())
        val strings = listOf("en", "hi").associateWith { locale ->
            LevelValidator.strictJson.parseToJsonElement(content.resolve("strings/$locale.json").readText())
                .jsonObject.keys
        }
        val context = ValidationContext(
            supportedSchemaVersions = setOf(1),
            supportedRuntimes = setOf("native-v1"),
            appVersion = 1,
            assets = catalog.assets.mapValues { it.value.kind },
            strings = strings,
        )
        val levelFiles = Files.list(content.resolve("levels")).use { stream ->
            stream.filter { it.extension == "json" }.sorted().toList()
        }
        assertTrue(levelFiles.isNotEmpty())
        val levels = levelFiles.map { file ->
            val json = file.readText()
            val issues = LevelValidator.validate(json, context)
            assertEquals(emptyList(), issues, "${file.name}: $issues")
            LevelValidator.decode(json)
        }
        assertEquals(levels.size, levels.map { it.id }.distinct().size, "Level ids must be unique")
        for (level in levels) {
            assertTrue(SolutionReplayer.replay(level, level.solution.orEmpty()).completed, level.id)
            for (alternate in level.altSolutions) assertTrue(SolutionReplayer.replay(level, alternate).completed, level.id)
        }
    }
}
