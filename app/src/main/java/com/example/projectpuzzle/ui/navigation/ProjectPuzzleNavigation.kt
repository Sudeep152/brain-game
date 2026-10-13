package com.example.projectpuzzle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.projectpuzzle.ui.screens.HomeScreen
import com.example.projectpuzzle.ui.screens.PlayScreen
import com.example.projectpuzzle.ui.screens.SettingsScreen

private data object HomeRoute
private data object PlayRoute
private data object SettingsRoute

@Composable
fun ProjectPuzzleNavigation() {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLast()
            }
        },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    onPlay = { backStack.add(PlayRoute) },
                    onSettings = { backStack.add(SettingsRoute) },
                )
            }
            entry<PlayRoute> { PlayScreen() }
            entry<SettingsRoute> { SettingsScreen() }
        },
    )
}
