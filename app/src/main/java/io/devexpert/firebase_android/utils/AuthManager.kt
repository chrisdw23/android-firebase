package io.devexpert.firebase_android.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    suspend fun resetPassword(email: String): AuthRes<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthRes.Success(Unit)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al enviar el correo de recuperación")
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}