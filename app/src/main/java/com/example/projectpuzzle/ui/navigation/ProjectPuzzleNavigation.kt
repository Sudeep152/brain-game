package com.example.projectpuzzle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.projectpuzzle.ui.screens.HomeScreen
import com.example.projectpuzzle.ui.screens.PlayScreen
import com.example.projectpuzzle.ui.screens.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
private data object HomeRoute : NavKey

@Serializable
private data object PlayRoute : NavKey

@Serializable
private data object SettingsRoute : NavKey

@Composable
fun ProjectPuzzleNavigation() {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        onBack = {
            // NavDisplay only handles Back when another destination can be popped.
            // At Home, Android receives Back and exits the activity normally.
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
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
