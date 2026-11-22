package io.devexpert.firebase_android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.devexpert.firebase_android.utils.AnalyticsManager
import io.devexpert.firebase_android.utils.AuthManager import io.devexpert.firebase_android.utils.AuthRes
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    auth: AuthManager,
    analytics: AnalyticsManager,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
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
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Ingresa tu correo registrado") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    scope.launch {
                        when (val result = auth.resetPassword(email.trim())) {
                            is AuthRes.Success -> {
                                analytics.logEvent("recuperar_password_enviado")
                                Toast.makeText(context, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                                onNavigateBack()
                            }
                            is AuthRes.Error -> {
                                Toast.makeText(context, result.errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enviar correo de recuperación")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateBack) {
            Text(text = "Volver al inicio de sesión")
        }
    }
}