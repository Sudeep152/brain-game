package com.example.projectpuzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.projectpuzzle.designsystem.ProjectPuzzleTheme
import com.example.projectpuzzle.ui.navigation.ProjectPuzzleNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectPuzzleTheme {
                ProjectPuzzleNavigation()
            }
        }
    }
}
