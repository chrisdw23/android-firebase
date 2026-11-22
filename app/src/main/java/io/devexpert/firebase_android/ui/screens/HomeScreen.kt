package io.devexpert.firebase_android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.devexpert.firebase_android.utils.AnalyticsManager
import io.devexpert.firebase_android.utils.AuthManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    auth: AuthManager,
    analytics: AnalyticsManager,
    onNavigateToLogin: () -> Unit
) {
    val user = auth.getCurrentUser()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        analytics.logScreenView("HomeScreen")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user?.photoUrl != null) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar de usuario",
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (!user?.displayName.isNullOrBlank()) {
                "Bienvenido, ${user?.displayName}"
            } else {
                "Bienvenido a Home"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (user?.isAnonymous == true) {
                "Sesión: Invitado (${user.uid.take(6)}...)"
            } else {
                "Correo: ${user?.email ?: "Sin correo"}"
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    auth.signOut(context)
                    onNavigateToLogin()
                }
            }
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}