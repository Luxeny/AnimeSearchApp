package com.example.animesearchapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.animesearchapp.presentation.navigation.AppNavGraph
import com.example.animesearchapp.presentation.ui.theme.AnimeSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimeSearchTheme {
                AppNavGraph()
            }
        }
    }
}
