package io.devexpert.firebase_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.devexpert.firebase_android.ui.navigation.AppNavigation
import io.devexpert.firebase_android.ui.theme.Firebase_androidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Firebase_androidTheme {
                AppNavigation()
            }
        }
    }
}