package com.example.timetableapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetableapp.navigation.AppNavHost
import com.example.timetableapp.ui.settings.SettingsViewModel
import com.example.timetableapp.ui.theme.AppTheme
import com.example.timetableapp.ui.theme.DefaultSeed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    val settingsVm: SettingsViewModel = hiltViewModel()
    val state by settingsVm.state.collectAsState()

    val dark = when (state.themeMode) {
        com.example.timetableapp.data.settings.ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
        com.example.timetableapp.data.settings.ThemeMode.DARK -> true
        com.example.timetableapp.data.settings.ThemeMode.LIGHT -> false
    }

    AppTheme(
        darkTheme = dark,
        seedColor = androidx.compose.ui.graphics.Color(state.seedColor.toInt()),
        useDynamicColor = state.useDynamicColor,
        textScale = state.textScale
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                AppNavHost()
            }
        }
    }
}
