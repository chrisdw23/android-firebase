package io.devexpert.firebase_android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.devexpert.firebase_android.utils.AnalyticsManager
import io.devexpert.firebase_android.utils.AuthManager
import io.devexpert.firebase_android.utils.AuthRes
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    auth: AuthManager,
    analytics: AnalyticsManager,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch {
                    when (val result = auth.signInAnonymously()) {
                        is AuthRes.Success -> {
                            analytics.logEvent("login_anonimo_exitoso")
                            onNavigateToHome()
                        }
                        is AuthRes.Error -> {
                            analytics.logError(result.errorMessage)
                            Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        ) {
            Text(text = "Continuar como invitado")
        }
    }
}