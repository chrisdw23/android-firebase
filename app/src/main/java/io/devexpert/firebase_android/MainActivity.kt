package io.devexpert.firebase_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.devexpert.firebase_android.ui.theme.Firebase_androidTheme
import io.devexpert.firebase_android.utils.AnalyticsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Firebase_androidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnalyticsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val analytics = remember { AnalyticsManager(context) }

    // Seguimiento de pantalla seguro ante recomposiciones
    LaunchedEffect(Unit) {
        analytics.logScreenView("MainScreen")
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Firebase Analytics Activo")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val params = Bundle().apply {
                putString("opcion", "tema_oscuro")
            }
            analytics.logEvent("configuracion", params)
        }) {
            Text(text = "Registrar evento: Configuración")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            analytics.logError("Error simulado desde Compose")
        }) {
            Text(text = "Registrar evento: Usuario Error")
        }
    }
}