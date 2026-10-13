package com.example.projectpuzzle.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.projectpuzzle.R
import com.example.projectpuzzle.ui.design.DesignTokens

@Composable
fun HomeScreen(onPlay: () -> Unit, onSettings: () -> Unit) {
    PlaceholderLayout(titleRes = R.string.home_title) {
        Text(text = stringResource(R.string.home_subtitle))
        Button(onClick = onPlay) {
            Text(text = stringResource(R.string.play_button))
        }
        Button(onClick = onSettings) {
            Text(text = stringResource(R.string.settings_button))
        }
    }
}

@Composable
fun PlayScreen() {
    PlaceholderLayout(titleRes = R.string.play_title) {
        Text(text = stringResource(R.string.play_placeholder))
    }
}

@Composable
fun SettingsScreen() {
    PlaceholderLayout(titleRes = R.string.settings_title) {
        Text(text = stringResource(R.string.settings_placeholder))
    }
}

@Composable
private fun PlaceholderLayout(
    @StringRes titleRes: Int,
    content: @Composable () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(DesignTokens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(
                DesignTokens.ContentSpacing,
                Alignment.CenterVertically,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(titleRes),
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            )
            content()
        }
    }
}
