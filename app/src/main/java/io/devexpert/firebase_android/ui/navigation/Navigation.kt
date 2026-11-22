package io.devexpert.firebase_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.devexpert.firebase_android.ui.screens.ForgotPasswordScreen
import io.devexpert.firebase_android.ui.screens.HomeScreen
import io.devexpert.firebase_android.ui.screens.LoginScreen
import io.devexpert.firebase_android.ui.screens.SignUpScreen
import io.devexpert.firebase_android.utils.AnalyticsManager
import io.devexpert.firebase_android.utils.AuthManager

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authManager = remember { AuthManager() }
    val analyticsManager = remember { AnalyticsManager(context) }

    val startDestination = if (authManager.getCurrentUser() != null) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                auth = authManager,
                analytics = analyticsManager,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                auth = authManager,
                analytics = analyticsManager,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                auth = authManager,
                analytics = analyticsManager,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("home") {
            HomeScreen(
                auth = authManager,
                analytics = analyticsManager,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}