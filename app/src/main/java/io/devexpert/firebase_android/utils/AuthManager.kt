package io.devexpert.firebase_android.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun Context.findActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    suspend fun signInAnonymously(): AuthRes<FirebaseUser?> {
        return try {
            val result = auth.signInAnonymously().await()
            AuthRes.Success(result.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión como invitado")
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al registrar usuario")
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    suspend fun signInWithGoogle(context: Context): AuthRes<FirebaseUser?> {
        return try {
            val activityContext = context.findActivity() ?: context
            val credentialManager = CredentialManager.create(activityContext)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("939499959364-1m27ltjodpn1jlghom5i62820rbrqfnn.apps.googleusercontent.com")
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                AuthRes.Success(authResult.user)
            } else {
                AuthRes.Error("Credencial no reconocida: ${credential.type}")
            }
        } catch (e: GetCredentialCancellationException) {
            AuthRes.Error("Inicio de sesión cancelado por el usuario")
        } catch (e: GetCredentialException) {
            AuthRes.Error("Error CredentialManager (${e.type}): ${e.errorMessage}")
        } catch (e: Exception) {
            AuthRes.Error("Error Firebase: ${e.message}")
        }
    }

    suspend fun resetPassword(email: String): AuthRes<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthRes.Success(Unit)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al enviar el correo de recuperación")
        }
    }

    suspend fun signOut(context: Context? = null) {
        auth.signOut()
        if (context != null) {
            try {
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}