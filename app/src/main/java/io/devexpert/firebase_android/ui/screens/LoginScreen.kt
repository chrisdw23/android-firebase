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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.devexpert.firebase_android.utils.AnalyticsManager
import io.devexpert.firebase_android.utils.AuthManager
import io.devexpert.firebase_android.utils.AuthRes
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    auth: AuthManager,
    analytics: AnalyticsManager,
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        when (val result = auth.signInWithEmailAndPassword(email.trim(), password)) {
                            is AuthRes.Success -> {
                                analytics.logEvent("login_email_exitoso")
                                onNavigateToHome()
                            }
                            is AuthRes.Error -> {
                                analytics.logError(result.errorMessage)
                                Toast.makeText(context, result.errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Entrar")
        }

        TextButton(onClick = onNavigateToForgotPassword) {
            Text(text = "¿Olvidaste tu contraseña?")
        }

        TextButton(onClick = onNavigateToSignUp) {
            Text(text = "¿No tienes cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Continuar como invitado")
        }
    }
}